package com.github.tomboyo.brainstorm.routes;

import static com.github.tomboyo.brainstorm.util.Functions.tunneledFunction;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;

import com.github.tomboyo.brainstorm.graph.GraphService;
import com.github.tomboyo.brainstorm.graph.command.Update;
import com.github.tomboyo.brainstorm.processor.AdocExtract;
import com.github.tomboyo.brainstorm.util.FileUtil;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DocumentIngestion extends RouteBuilder {
	private static final String logPrefix =
		DocumentIngestion.class.getName();

	@Autowired @Qualifier("notebook.directory")
	private Path notebookDirectory;

	@Autowired
	private GraphService graphService;

	@Override
	public void configure() throws Exception {
		fromF("file-watch:%s?events=CREATE,MODIFY", notebookDirectory)
			.transform().body(File.class, File::toPath)
			.filter().body(Path.class, FileUtil.hasExtension("adoc"))
			.toF("log:%s.adocFileModified?level=INFO", logPrefix)
			.transform().body(
				Path.class, tunneledFunction(AdocExtract::extractUpdate))
			.toF("log:%s.adocUpdate?level=DEBUG", logPrefix)
			.process().body(Update.class, graphService::update);
		
		fromF("file-watch:%s?events=DELETE", notebookDirectory)
			.transform().body(File.class, File::toPath)
			.filter().body(Path.class, FileUtil.hasExtension("adoc"))
			.toF("log:%s.adocFileDeleted?level=INFO", logPrefix)
			.transform()
				// file.toUri().toStirng() != file.toPath().toUri().toString()
				// This matters to the graph service, since our routes make use
				// of Path as an intermediary representation.
				.body(Path.class, path -> path.toUri())
			.process()
				.body(URI.class, graphService::delete);
	}
}