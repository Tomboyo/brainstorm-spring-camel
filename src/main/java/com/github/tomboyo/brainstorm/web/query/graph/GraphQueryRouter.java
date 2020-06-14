package com.github.tomboyo.brainstorm.web.query.graph;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class GraphQueryRouter {
	@Bean
	public static RouterFunction<ServerResponse> route(
		GraphQueryHandler handler
	) {
		return RouterFunctions.route(
			RequestPredicates.GET("/graph"),
			handler::query);
	}
}