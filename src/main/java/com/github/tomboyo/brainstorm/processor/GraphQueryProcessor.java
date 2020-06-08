package com.github.tomboyo.brainstorm.processor;

import static org.apache.camel.Exchange.CONTENT_TYPE;
import static org.apache.camel.Exchange.HTTP_RESPONSE_CODE;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.tomboyo.brainstorm.graph.GraphService;
import com.github.tomboyo.brainstorm.graph.model.Graph;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GraphQueryProcessor implements Processor {
	@Autowired
	GraphService graphService;

	@Override
	public void process(Exchange exchange) throws Exception {
		var in = exchange.getIn();
		var message = exchange.getMessage();

		var locationString = in.getHeader("location", String.class);
		Path location;
		try {
			location = Paths.get(locationString);
		} catch (InvalidPathException e) {
			invalidLocation(message, locationString);
			return;
		}

		graphService.query(location).ifPresentOrElse(
			(graph) -> ok(message, graph),
			() -> notFound(message));
	}

	private static void invalidLocation(
		Message message,
		String locationString
	) {
		message.setHeader(HTTP_RESPONSE_CODE, 400);
		message.setHeader(CONTENT_TYPE, "text/plain");
		message.setBody(String.format(
			"location=%s is not a valid location path", locationString));
	}

	private static void ok(
		Message message,
		Graph graph
	) {
		message.setBody(graph);
	}

	private static void notFound(
		Message message
	) {
		message.setHeader(HTTP_RESPONSE_CODE, 404);
		message.setBody(null);
	}
}