package com.github.tomboyo.brainstorm.routes;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;

import com.github.tomboyo.brainstorm.graph.GraphService;
import com.github.tomboyo.brainstorm.predicate.IsAdocFile;
import com.github.tomboyo.brainstorm.processor.AdocDocumentUpdateProcessor;
import com.github.tomboyo.brainstorm.processor.GraphUpdateProcessor;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DocumentIngestion extends RouteBuilder {
	@Autowired @Qualifier("notebook.directory")
	private Path notebookDirectory;

	@Autowired
	private IsAdocFile isAdocFile;

	@Autowired
	private AdocDocumentUpdateProcessor toUpdate;

	@Autowired
	private GraphUpdateProcessor graphUpdate;

	@Autowired
	private GraphService graphService;

	@Override
	public void configure() throws Exception {
		fromF("file-watch:%s?events=CREATE,MODIFY", notebookDirectory)
			.filter(isAdocFile)
			.to("log:adoc.file.modified?level=INFO")
			.process(toUpdate)
			.process(graphUpdate);
		
		fromF("file-watch:%s?events=DELETE", notebookDirectory)
			.filter(isAdocFile)
			.to("log:adoc.file.deleted?level=INFO")
			.transform()
				// file.toUri().toStirng() != file.toPath().toUri().toString()
				// This matters to the graph service, since our routes make use
				// of Path as an intermediary representation.
				.body(File.class, file -> file.toPath().toUri())
			.process()
				.body(URI.class, graphService::delete);
	}
}