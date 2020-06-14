package com.github.tomboyo.brainstorm.web.query.graph;

import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

import com.github.tomboyo.brainstorm.graph.GraphService;
import com.github.tomboyo.brainstorm.graph.model.Document;
import com.github.tomboyo.brainstorm.graph.model.Graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.reactive.server.WebTestClient;

public class GraphQueryTest {
	@Mock GraphService graphService;

	private WebTestClient client;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		client = WebTestClient.bindToRouterFunction(
				GraphQueryRouter.route(new GraphQueryHandler(graphService)))
			.configureClient()
			.baseUrl("/graph")
			.defaultHeader("Accept", "application/json")
			.build();
	}
	
	@Test
	public void missingLocation() {
		client.get().exchange()
			.expectStatus().isEqualTo(BAD_REQUEST)
			.expectBody(String.class)
				.isEqualTo("location parameter must be an absolute path");
		
		verifyNoInteractions(graphService);
	}

	@Test
	public void emptyLocation() {
		client.get().uri("?location=").exchange()
			.expectStatus().isEqualTo(BAD_REQUEST)
			.expectBody(String.class)
				.isEqualTo("location parameter must be an absolute path");
		
		verifyNoInteractions(graphService);
	}

	@Test
	public void relativeLocation() {
		client.get().uri("?location=foo/bar/baz").exchange()
			.expectStatus().isEqualTo(BAD_REQUEST)
			.expectBody(String.class)
				.isEqualTo("location parameter must be an absolute path");
		
		verifyNoInteractions(graphService);
	}

	@Test
	public void documentNotFound() {
		when(graphService.query(eq(Paths.get("/foo"))))
			.thenReturn(Optional.empty());
		
		client.get().uri("?location=/foo").exchange()
			.expectStatus().isEqualTo(NOT_FOUND);
	}

	@Test
	public void ok() {
		var expectedBody = new Graph(
			new Document(Paths.get("/foo")),
			Set.of(),
			Set.of());
		when(graphService.query(eq(Paths.get("/foo"))))
			.thenReturn(Optional.of(expectedBody));
		
		client.get().uri("?location=/foo").exchange()
			.expectStatus().isEqualTo(OK)
			.expectBody(Graph.class).isEqualTo(expectedBody);
	}

	@Test
	public void denormalizedLocationPath() {
		var expectedBody = new Graph(
			new Document(Paths.get("/bar")),
			Set.of(),
			Set.of());
		when(graphService.query(eq(Paths.get("/bar"))))
			.thenReturn(Optional.of(expectedBody));
		
		client.get().uri("?location=/foo/.././bar/./").exchange()
			.expectStatus().isEqualTo(OK)
			.expectBody(Graph.class).isEqualTo(expectedBody);
	}
}