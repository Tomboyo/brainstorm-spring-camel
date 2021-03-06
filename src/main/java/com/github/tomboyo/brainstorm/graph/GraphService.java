package com.github.tomboyo.brainstorm.graph;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import com.github.tomboyo.brainstorm.graph.command.Update;
import com.github.tomboyo.brainstorm.graph.model.Document;
import com.github.tomboyo.brainstorm.graph.model.Graph;
import com.github.tomboyo.brainstorm.graph.model.Reference;

import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class GraphService {
	private static final Logger logger =
		LoggerFactory.getLogger(GraphService.class);

	private final DatabaseManagementService management;
	private final GraphDatabaseService db;

	@Autowired
	public GraphService(
		@Qualifier("data.directory") File dataDir
	) {
		management = new DatabaseManagementServiceBuilder(dataDir)
			.setConfig(BoltConnector.enabled, true)
			.setConfig(BoltConnector.listen_address,
				new SocketAddress("localhost", 7687))
			.build();

		db = management.database("neo4j");
	}

	@PreDestroy
	public void shutdown() {
		management.shutdown();
	}

	public Optional<Graph> query(Path location) {
		logger.debug("Processing query at Path={}", location);

		try (var tx = db.beginTx()) {
			return findDocument(tx, location)
				.map(source -> new Graph(
					source,
					outboundReferences(tx, location),
					inboundReferences(tx, location)));
		}
	}

	private Optional<Document> findDocument(Transaction tx, Path location) {
		return tx.execute("""
			MATCH (source:Document)
			WHERE source.location = $location
			return true;
			""", Map.of("location", location.toString())
		).stream()
			.map(map -> new Document(location))
			.findFirst();
	}

	private Set<Reference> outboundReferences(Transaction tx, Path location) {
		return tx.execute("""
			MATCH (source:Document)-[r:Reference]->(dest:Document)
			WHERE source.location = $location
			RETURN
				r.context, dest.location
			""", Map.of("location", location.toString())
		).stream()
			.map(map -> new Reference(
				(String) map.get("r.context"),
				new Document(Paths.get((String) map.get("dest.location")))))
			.collect(Collectors.toSet());
	}

	private Set<Reference> inboundReferences(Transaction tx, Path location) {
		return tx.execute(
			"""
			MATCH (source:Document)<-[r:Reference]-(dest:Document)
			WHERE source.location = $location
			RETURN
				r.context, dest.location
			""", Map.of("location", location.toString())
		).stream()
			.map(map -> new Reference(
				(String) map.get("r.context"),
				new Document(Paths.get((String) map.get("dest.location")))))
			.collect(Collectors.toSet());
	}

	public void update(Update update) {
		logger.debug("Processing update for Update={}", update);
		
		try (var tx = db.beginTx()) {
			mergeDocument(tx, update.source());
			removeOutboundReferences(tx, update.source());

			update.outboundReferences().stream().forEach(ref -> {
				mergeDocument(tx, ref.destination());
				createOutboundReference(tx, update.source(), ref);
			});

			tx.commit();
		}
	}

	private static void mergeDocument(
		Transaction tx,
		Document document
	) {
		tx.execute(
			"MERGE (:Document {location: $location})",
			Map.of("location", document.location().toString()));
	}

	private static void removeOutboundReferences(
		Transaction tx,
		Document document
	) {
		tx.execute(
			"""
			MATCH (source:Document)-[r:Reference]->(:Document)
			WHERE source.location = $location
			DELETE r
			""",
			Map.of("location", document.location().toString()));
	}

	private static void createOutboundReference(
		Transaction tx,
		Document source,
		Reference reference
	) {
		tx.execute(
			"""
			MATCH (source:Document), (destination:Document)
			WHERE source.location = $source
			AND destination.location = $destination
			CREATE (source)-[reference:Reference]->(destination)
			SET reference.context = $context
			""", Map.of(
				"source", source.location().toString(),
				"destination", reference.destination().location().toString(),
				"context", reference.context()
			));
	}

	public void delete(Path location) {
		logger.debug("Processing delete at Path={}", location);

		try (var tx = db.beginTx()) {
			// Delete all outbound references from the deleted Document
			tx.execute(
				"""
				MATCH (source:Document)-[r:Reference]->(:Document)
				WHERE source.location = $location
				DELETE r
				""", Map.of("location", location.toString()));
			
			// If the Document is detached, furthermore delete it. But leave the
			// Document if other documents still reference it.
			tx.execute(
				"""
				MATCH (source:Document)
				WHERE source.location = $location
				AND NOT (source)<-[:Reference]-(:Document)
				DELETE source
				""", Map.of("location", location.toString()));
			
			tx.commit();
		}
	}
}