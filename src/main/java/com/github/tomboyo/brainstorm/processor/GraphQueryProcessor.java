package com.github.tomboyo.brainstorm.processor;

import static org.apache.camel.Exchange.HTTP_RESPONSE_CODE;

import java.net.URI;

import com.github.tomboyo.brainstorm.graph.GraphService;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GraphQueryProcessor implements Processor {
	private static final Logger logger =
		LoggerFactory.getLogger(GraphQueryProcessor.class);
	
	@Autowired
	GraphService graphService;

	@Override
	public void process(Exchange exchange) throws Exception {
		var in = exchange.getIn();
		var message = exchange.getMessage();

		URI location;
		try {
			location = new URI(in.getHeader("location", String.class));
		} catch (Exception e) {
			message.setHeader(HTTP_RESPONSE_CODE, 400);
			message.setBody(null);
			return;
		}

		graphService.query(location).ifPresentOrElse(
			(graph) -> message.setBody(graph),
			() -> {
				message.setHeader(HTTP_RESPONSE_CODE, 404);
				message.setBody(null);
			});
	}
}