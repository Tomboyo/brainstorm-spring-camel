package com.github.tomboyo.brainstorm.routes;

import com.github.tomboyo.brainstorm.processor.GraphQueryProcessor;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GraphQuery extends RouteBuilder {
	private static final String logPrefix =
		GraphQuery.class.getName();
	
	@Autowired
	private GraphQueryProcessor graphQuery;
	
	@Override
	public void configure() throws Exception {
		restConfiguration()
			.component("netty-http")
			.port("8080")
			.host("localhost")
			.bindingMode("auto");

		rest("/graph")
			.get("/")
				.produces("application/json")
			.route()
				.toF("log:%s.query?level=INFO", logPrefix)
				.process(graphQuery);
	}
}