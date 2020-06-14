package com.github.tomboyo.brainstorm.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

import com.github.tomboyo.brainstorm.graph.command.Update;
import com.github.tomboyo.brainstorm.graph.model.Document;
import com.github.tomboyo.brainstorm.graph.model.Graph;
import com.github.tomboyo.brainstorm.graph.model.Reference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class GraphServiceTest {
	private GraphService subject;

	@BeforeEach
	public void setup(
		@TempDir Path dataDir
	) throws Exception {
		subject = new GraphService(dataDir.toFile());
	}

	@AfterEach
	public void tearDown() {
		subject.shutdown();
	}

	/**
	 * After submitting an Update to the GraphService, the contents of that
	 * update may be retrieved with a corresponding Query.
	 */
	@Test
	public void updateAndQuery() {
		subject.update(new Update(
			new Document(Paths.get("foo")),
			Set.of(
				new Reference(
					"context for bar",
					new Document(Paths.get("bar"))),
				new Reference(
					"context for baz",
					new Document(Paths.get("baz"))))));
		
		var expected = Optional.of(new Graph(
			new Document(Paths.get("foo")),
			Set.of(
				new Reference(
					"context for bar",
					new Document(Paths.get("bar"))),
				new Reference(
					"context for baz",
					new Document(Paths.get("baz")))),
			Set.of()));
		
		assertEquals(expected, subject.query(Paths.get("foo")));
	}

	/**
	 * If we delete a document which is referenced by others, then the document
	 * and its inbound references are preserved, but its outbound references are
	 * deleted.
	 */
	@Test
	public void deleteReferencedDocument() {
		subject.update(new Update(
			new Document(Paths.get("A")),
			Set.of(
				new Reference(
					"A -> B",
					new Document(Paths.get("B"))))));
		subject.update(new Update(
			new Document(Paths.get("B")),
			Set.of(
				new Reference(
					"B -> A",
					new Document(Paths.get("A"))))));
		
		subject.delete(Paths.get("A"));

		var actual = subject.query(Paths.get("A"));
		var expected = Optional.of(new Graph(
			new Document(Paths.get("A")),
			Set.of(), // no outbound references
			Set.of(
				new Reference(
					"B -> A",
					new Document(Paths.get("B"))))));
		
		assertEquals(expected, actual);
	}

	/**
	 * If we delete a document which is not referenced by others ("orphaned"),
	 * then both it and all of its outbound references are deleted.
	 */
	@Test
	public void deleteUnreferencedDocument() {
		subject.update(new Update(
			new Document(Paths.get("A")),
			Set.of(
				new Reference(
					"A -> B",
					new Document(Paths.get("B"))))));
		
		subject.delete(Paths.get("A"));
		var actual = subject.query(Paths.get("A"));
		var expected = Optional.empty();

		assertEquals(expected, actual);
	}
}