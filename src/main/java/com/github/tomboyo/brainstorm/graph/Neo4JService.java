package com.github.tomboyo.brainstorm.graph;

import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

import javax.annotation.PreDestroy;

import com.github.tomboyo.brainstorm.graph.model.Reference;

import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Neo4JService {
	private final DatabaseManagementService management;
	private final GraphDatabaseService db;

	public Neo4JService(
		@Value("${data.directory}") String dataDirectory
	) {
		management = new DatabaseManagementServiceBuilder(
				Paths.get(dataDirectory).toFile())
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

	public void updateReferences(Set<Reference> references) {
		// TODO: no references is absolutely possible.
		// TODO: how do we handle node deletion? Need to make sure
		// deletion events are marked separately.
		var anyRef = references.stream().findAny().get();

		try (var tx = db.beginTx()) {
			ensureSource(tx, anyRef.toMap());
			references.stream().forEach(ref -> {
				var params = ref.toMap();
				ensureDestination(tx, params);
				removeOldReferences(tx, params);
				createNewReferences(tx, params);
			});
			tx.commit();
		}
	}

	private static void ensureSource(
		Transaction tx,
		Map<String, Object> params
	) {
		tx.execute("MERGE (:Document {location: $source})", params);
	}

	private static void ensureDestination(
		Transaction tx,
		Map<String, Object> params
	) {
		tx.execute("MERGE (:Document {location: $destination})", params);
	}

	private static void removeOldReferences(
		Transaction tx,
		Map<String, Object> params
	) {
		tx.execute(
			"""
			MATCH (f:Document)-[r:Reference]->(t:Document)
			WHERE f.location = $source
			AND t.location = $destination
			DELETE r
			""", params);
	}

	private static void createNewReferences(
		Transaction tx,
		Map<String, Object> params
	) {
		tx.execute(
			"""
			MATCH (f:Document), (t:Document)
			WHERE f.location = $source
			AND t.location = $destination
			CREATE (f)-[r:Reference]->(t)
			SET r.context = $context
			""", params);
	}
}