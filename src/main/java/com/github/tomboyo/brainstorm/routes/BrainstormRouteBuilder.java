package com.github.tomboyo.brainstorm.routes;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;

import com.github.tomboyo.brainstorm.graph.GraphService;
import com.github.tomboyo.brainstorm.predicate.IsAdocFile;
import com.github.tomboyo.brainstorm.processor.AdocDocumentUpdateProcessor;
import com.github.tomboyo.brainstorm.processor.GraphQueryProcessor;
import com.github.tomboyo.brainstorm.processor.GraphUpdateProcessor;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class BrainstormRouteBuilder extends RouteBuilder {
	@Autowired @Qualifier("notebook.directory")
	private Path notebookDirectory;

	@Autowired
	private GraphService graphService;

	@Autowired
	private IsAdocFile isAdocFile;

	@Autowired
	private AdocDocumentUpdateProcessor documentUpdate;

	@Autowired
	private GraphUpdateProcessor graphUpdate;

	@Autowired
	private GraphQueryProcessor graphQuery;
	
	@Override
	public void configure() throws Exception {
		fromF("file-watch:%s?events=CREATE,MODIFY", notebookDirectory)
			.filter(isAdocFile)
			.to("log:file.change?level=INFO")
			.process(documentUpdate)
			.to("log:file.processed?level=DEBUG")
			.process(graphUpdate);
		
		fromF("file-watch:%s?events=DELETE", notebookDirectory)
			.filter(isAdocFile)
			.transform()
				.body(File.class, File::toURI)
			.to("log:file.change?level=INFO")
			.process()
				.body(URI.class, graphService::delete);

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
				.process(graphQuery);
	}
}