package com.github.tomboyo.brainstorm.routes;

import com.github.tomboyo.brainstorm.configuration.PropertyConfig;
import com.github.tomboyo.brainstorm.predicate.FileHasExtension;
import com.github.tomboyo.brainstorm.processor.DocumentReferenceProcessor;
import com.github.tomboyo.brainstorm.processor.GraphProcessor;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class BrainstormRouteBuilder extends RouteBuilder {
	@Autowired
	private PropertyConfig config;

	@Autowired
	private FileHasExtension fileHasExtension;

	@Autowired
	private DocumentReferenceProcessor documentReferenceProcessor;

	@Autowired
	private GraphProcessor graphProcessor;
	
	@Override
	public void configure() throws Exception {
		from("file-watch:" + config.notebookDirectory())
			.filter(fileHasExtension)
			.to("log:file.change?level=INFO")
			.to("vm:file.change");
	
		from("vm:file.change")
			.process(documentReferenceProcessor)
			.to("log:file.processed?level=INFO")
			.to("vm:graph.update");
		
		from("vm:graph.update")
			.process(graphProcessor);
	}
}