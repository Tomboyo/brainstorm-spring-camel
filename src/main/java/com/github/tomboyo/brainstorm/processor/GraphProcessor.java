package com.github.tomboyo.brainstorm.processor;

import java.util.Set;

import com.github.tomboyo.brainstorm.graph.Neo4JService;
import com.github.tomboyo.brainstorm.graph.model.Reference;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GraphProcessor implements Processor {
	@Autowired
	private Neo4JService graph;

	@Override
	@SuppressWarnings("Unchecked")
	public void process(Exchange exchange) throws Exception {
		graph.updateReferences(
			(Set<Reference>) exchange.getIn().getBody());
	}
}