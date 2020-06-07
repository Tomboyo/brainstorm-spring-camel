package com.github.tomboyo.brainstorm.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import com.github.tomboyo.brainstorm.graph.command.Update;
import com.github.tomboyo.brainstorm.graph.model.Document;
import com.github.tomboyo.brainstorm.graph.model.Graph;
import com.github.tomboyo.brainstorm.graph.model.Reference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
			new Document(URI.create("foo")),
			Set.of(
				new Reference(
					"context for bar",
					new Document(URI.create("bar"))),
				new Reference(
					"context for baz",
					new Document(URI.create("baz"))))));
		
		var expected = Optional.of(new Graph(
			new Document(URI.create("foo")),
			Set.of(
				new Reference(
					"context for bar",
					new Document(URI.create("bar"))),
				new Reference(
					"context for baz",
					new Document(URI.create("baz")))),
			Set.of()));
		
		assertEquals(expected, subject.query(URI.create("foo")));
	}

	/**
	 * If we delete a document which is referenced by others, then the document
	 * and its inbound references are preserved, but its outbound references are
	 * deleted.
	 */
	@Test
	public void deleteReferencedDocument() {
		subject.update(new Update(
			new Document(URI.create("A")),
			Set.of(
				new Reference(
					"A -> B",
					new Document(URI.create("B"))))));
		subject.update(new Update(
			new Document(URI.create("B")),
			Set.of(
				new Reference(
					"B -> A",
					new Document(URI.create("A"))))));
		
		subject.delete(URI.create("A"));

		var actual = subject.query(URI.create("A"));
		var expected = Optional.of(new Graph(
			new Document(URI.create("A")),
			Set.of(), // no outbound references
			Set.of(
				new Reference(
					"B -> A",
					new Document(URI.create("B"))))));
		
		assertEquals(expected, actual);
	}

	/**
	 * If we delete a document which is not referenced by others ("orphaned"),
	 * then both it and all of its outbound references are deleted.
	 */
	@Test
	public void deleteUnreferencedDocument() {
		subject.update(new Update(
			new Document(URI.create("A")),
			Set.of(
				new Reference(
					"A -> B",
					new Document(URI.create("B"))))));
		
		subject.delete(URI.create("A"));
		var actual = subject.query(URI.create("A"));
		var expected = Optional.empty();

		assertEquals(expected, actual);
	}
}