package com.github.tomboyo.brainstorm.web.query.graph;

import static java.util.function.Predicate.not;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import com.github.tomboyo.brainstorm.graph.GraphService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
public class GraphQueryHandler {
	private GraphService graphService;
	
	@Autowired
	public GraphQueryHandler(
		GraphService graphService
	) {
		this.graphService = graphService;
	}

	public Mono<ServerResponse> query(
		ServerRequest request
	) {
		var optLocation = getLocation(request);
		if (optLocation.isEmpty())
			return badLocation();
		
		var location = optLocation.get();
		if (!location.isAbsolute())
			return badLocation();
		
		return graphService.query(location)
			.map((graph) -> ServerResponse.ok()
				.contentType(APPLICATION_JSON)
				.bodyValue(graph))
			.orElseGet(() -> ServerResponse.notFound().build());
	}

	private static Mono<ServerResponse> badLocation() {
		return ServerResponse.badRequest()
			.bodyValue("location parameter must be an absolute path");
	}

	private static Optional<Path> getLocation(ServerRequest request) {
		try {
			return request.queryParam("location")
				.filter(not(String::isBlank))
				.map(Paths::get)
				.map(Path::normalize);
		} catch (InvalidPathException e) {
			return Optional.empty();
		}
	}
}