package com.github.tomboyo.brainstorm.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import com.github.tomboyo.brainstorm.graph.command.Update;
import com.github.tomboyo.brainstorm.graph.model.Document;
import com.github.tomboyo.brainstorm.graph.model.Graph;
import com.github.tomboyo.brainstorm.graph.model.Reference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class GraphServiceTest {
	@TempDir
	protected Path tempDir;

	private GraphService subject;

	@BeforeEach
	public void setup() throws Exception {
		var dataDir = tempDir.resolve("test-data");
		Files.createDirectories(dataDir);
		subject = new GraphService(dataDir.toFile());
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
		
		var expected = new Graph(
			new Document(URI.create("foo")),
			Set.of(
				new Reference(
					"context for bar",
					new Document(URI.create("bar"))),
				new Reference(
					"context for baz",
					new Document(URI.create("baz")))),
			Set.of());
		
		assertEquals(expected, subject.query(URI.create("foo")));
	}
}