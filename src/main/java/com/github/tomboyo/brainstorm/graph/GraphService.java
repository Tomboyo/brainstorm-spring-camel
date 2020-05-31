package com.github.tomboyo.brainstorm.graph;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import com.github.tomboyo.brainstorm.configuration.PropertyConfig;
import com.github.tomboyo.brainstorm.graph.command.Query;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GraphService {
	private final DatabaseManagementService management;
	private final GraphDatabaseService db;

	@Autowired
	public GraphService(
		PropertyConfig config
	) {
		management = new DatabaseManagementServiceBuilder(
				config.dataDirectory().toFile())
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

	public Graph query(Query query) {
		var source = new Document(query.location());
		Set<Reference> references;
		try (var tx = db.beginTx()) {
			var result = tx.execute(
				"""
				MATCH (a:Document)-[r]-(b:Document)
				WHERE a.location = $location
				RETURN r.context, b.location
				""", query.toMap());
			
			references = result.stream()
				.map(map -> {
					System.out.println(map);
					var destination = Document.fromString(
						(String) map.get("b.location"));
					var context = (String) map.get("r.context");
					return new Reference(source, destination, context);
				})
				.collect(Collectors.toSet());
		}
		
		return new Graph(source, references);
	}

	public void update(Update update) {
		try (var tx = db.beginTx()) {
			var source = update.source().toMap();
			mergeDocument(tx, source);
			removeOutboundReferences(tx, source);

			update.outboundReferences().stream().forEach(ref -> {
				mergeDocument(tx, ref.destination().toMap());
				createOutboundReference(tx, ref.toMap());
			});

			tx.commit();
		}
	}

	private static void mergeDocument(
		Transaction tx,
		Map<String, Object> params
	) {
		tx.execute("MERGE (:Document {location: $location})", params);
	}

	private static void removeOutboundReferences(
		Transaction tx,
		Map<String, Object> params
	) {
		tx.execute(
			"""
			MATCH (source:Document)-[r:Reference]->(:Document)
			WHERE source.location = $location
			DELETE r
			""", params);
	}

	private static void createOutboundReference(
		Transaction tx,
		Map<String, Object> params
	) {
		tx.execute(
			"""
			MATCH (source:Document), (destination:Document)
			WHERE source.location = $source.location
			AND destination.location = $destination.location
			CREATE (source)-[reference:Reference]->(destination)
			SET reference.context = $context
			""", params);
	}
}