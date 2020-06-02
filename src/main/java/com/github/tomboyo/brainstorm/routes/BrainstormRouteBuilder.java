package com.github.tomboyo.brainstorm.routes;

import com.github.tomboyo.brainstorm.configuration.PropertyConfig;
import com.github.tomboyo.brainstorm.predicate.FileHasExtension;
import com.github.tomboyo.brainstorm.processor.AdocDocumentUpdateProcessor;
import com.github.tomboyo.brainstorm.processor.GraphQueryProcessor;
import com.github.tomboyo.brainstorm.processor.GraphUpdateProcessor;

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
	private AdocDocumentUpdateProcessor documentReferenceProcessor;

	@Autowired
	private GraphUpdateProcessor graphUpdateProcessor;

	@Autowired
	private GraphQueryProcessor graphQueryProcessor;
	
	@Override
	public void configure() throws Exception {
		fromF("file-watch:%s?events=CREATE,MODIFY",
				config.notebookDirectory())
			.filter(fileHasExtension)
			.to("log:file.change?level=INFO")
			.process(documentReferenceProcessor)
			.to("log:file.processed?level=INFO")
			.process(graphUpdateProcessor);

		restConfiguration()
			.component("netty-http")
			.port("8080")
			.host("localhost");
		
		rest("/graph")
			.get("/")
				.produces("application/json")
			.route()
				.to("log:query?level=INFO")
				.process(graphQueryProcessor);
	}
}