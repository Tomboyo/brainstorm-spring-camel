package com.github.tomboyo.brainstorm.processor;

import com.github.tomboyo.brainstorm.graph.GraphService;
import com.github.tomboyo.brainstorm.graph.command.Update;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GraphUpdateProcessor implements Processor {
	@Autowired
	private GraphService graph;

	@Override
	public void process(Exchange exchange) throws Exception {
		graph.update(
			exchange.getIn().getBody(Update.class));
	}
}