package com.github.tomboyo.brainstorm.routes;

import java.io.File;
import java.nio.file.Paths;
import java.util.Set;

import com.github.tomboyo.brainstorm.configuration.PropertyConfig;
import com.github.tomboyo.brainstorm.predicate.FileHasExtension;
import com.github.tomboyo.brainstorm.processor.DocumentReferenceProcessor;
import com.github.tomboyo.brainstorm.util.PropertyUtil;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class BrainstormRouteBuilder extends RouteBuilder {
	private final PropertyConfig config;

	@Autowired
	public BrainstormRouteBuilder(
		PropertyConfig config
	) {
		this.config = config;
	}
	
	@Override
	public void configure() throws Exception {
		from("file-watch:" + config.notebookDirectory())
			.filter(new FileHasExtension(config.notebookFileExtensions()))
			.to("log:file.change?level=DEBUG")
			.to("vm:file.change");
	
		from("vm:file.change")
			.process(new DocumentReferenceProcessor())
			.to("log:file.processed?level=DEBUG")
			.to("fm:graph.update");
		
	}
}