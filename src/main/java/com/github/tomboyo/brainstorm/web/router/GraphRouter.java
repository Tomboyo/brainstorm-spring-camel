package com.github.tomboyo.brainstorm.web.router;

import com.github.tomboyo.brainstorm.web.handler.GraphHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class GraphRouter {
	@Bean
	RouterFunction<ServerResponse> route(
		GraphHandler handler
	) {
		return RouterFunctions.route(
			RequestPredicates.GET("/graph"),
			handler::query);
	}
}