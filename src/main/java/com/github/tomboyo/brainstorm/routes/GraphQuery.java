package com.github.tomboyo.brainstorm.routes;

import java.util.Optional;

import com.github.tomboyo.brainstorm.processor.GraphQueryProcessor;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GraphQuery extends RouteBuilder {
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
				.to("log:query?level=INFO")
				.process(graphQuery)
				.process().exchange(exchange -> {
					var optional = exchange.getIn().getBody(Optional.class);
					if (optional.isPresent()) {
						exchange.getMessage().setBody(optional.get());
					} else {
						exchange.getMessage()
							.setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
					}
				});
	}
}