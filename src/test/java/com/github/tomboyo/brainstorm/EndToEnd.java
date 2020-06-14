package com.github.tomboyo.brainstorm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomboyo.brainstorm.graph.model.Document;
import com.github.tomboyo.brainstorm.graph.model.Graph;
import com.github.tomboyo.brainstorm.graph.model.Reference;

import org.apache.camel.ConsumerTemplate;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("tempDirs")
@Disabled("""
Test cases tend to finish before the graph service is 'ready' for requests. We
need to make sure tests block until the graph service is ready.
""")
public class EndToEnd {
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final HttpClient client = HttpClient.newHttpClient();

	@Autowired @Qualifier("notebook.directory")
	protected Path notebookDir;

	@Autowired
	protected ConsumerTemplate consumer;

	/**
	 * Create a new document in the notebook directory, then query to confirm it
	 * and its referenced documents exist.
	 */
	@Test
	public void createAndQuery() throws Exception {
		// Create the document within the notebook directory. The document
		// should automatically be detected and ingested.
		var document = notebookDir.resolve("A.adoc");
		Files.write(
			document,
			"""
			= My Document, "A"
			Contains a relative reference to <<B#foo,A-B>> and an absolute
			reference to <<C#foo,A-C>>.
			""".getBytes());
		
		// Await ingestion of the new document to complete.
		assertNotNull(
			consumer.receive("vm:ingestComplete", 5000),
			"Timed out waiting for document ingestion");
		
		// Query the service for the ingested document.
		var response = client.send(
			HttpRequest.newBuilder()
				.GET()
				.uri(URI.create(String.format(
					"http://localhost:8080/graph?location=%s",
					document)))
				.header("Accept", "application/json")
				.build(),
			BodyHandlers.ofString());

		assertEquals(200, response.statusCode());
		assertEquals(
			new Graph(
				new Document(document),
				Set.of(
					new Reference(
						"A-B",
						new Document(notebookDir.resolve("B.adoc"))),
					new Reference(
						"A-C",
						new Document(notebookDir.resolve("C.adoc")))),
				Set.of()),
			mapper.readValue(response.body(), Graph.class));
	}
}
