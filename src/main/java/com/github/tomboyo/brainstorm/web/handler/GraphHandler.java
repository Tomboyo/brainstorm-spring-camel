package com.github.tomboyo.brainstorm.web.handler;

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
public class GraphHandler {
	@Autowired
	GraphService graphService;

	public Mono<ServerResponse> query(
		ServerRequest request
	) {
		var location = getLocation(request);
		if (location.isEmpty()) {
			return ServerResponse.badRequest().build();
		} else {
			return location
				.flatMap(graphService::query)
				.map((graph) -> ServerResponse.ok()
					.contentType(APPLICATION_JSON)
					.bodyValue(graph))
				.orElseGet(() -> ServerResponse.notFound().build());
		}
	}

	private static Optional<Path> getLocation(ServerRequest request) {
		try {
			return request.queryParam("location").map(Paths::get);
		} catch (InvalidPathException e) {
			return Optional.empty();
		}
	}
}