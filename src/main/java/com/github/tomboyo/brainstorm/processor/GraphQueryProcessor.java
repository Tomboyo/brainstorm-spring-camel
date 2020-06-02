package com.github.tomboyo.brainstorm.processor;

import java.net.URI;

import com.github.tomboyo.brainstorm.graph.GraphService;
import com.github.tomboyo.brainstorm.graph.command.Query;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GraphQueryProcessor implements Processor {
	@Autowired
	GraphService graphService;

	@Override
	public void process(Exchange exchange) throws Exception {
		var message = exchange.getIn();
		var location = message.getHeader("location", String.class);
		// TODO: new URI may throw; this should equate to a 400.
		var graph = graphService.query(new Query(new URI(location)));
		exchange.getMessage().setBody(graph);
	}
}