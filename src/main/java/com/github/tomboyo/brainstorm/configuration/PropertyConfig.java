package com.github.tomboyo.brainstorm.configuration;

import java.io.File;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("${configuration.file}")
public class PropertyConfig {
	@Bean("data.directory")
	public File dataDirectory(
		@Value("${data.directory}") String dir
	) {
		return new File(dir);
	}

	@Bean("notebook.directory")
	public Path notebookDirectory(
		@Value("${notebook.directory}") String dir
	) {
		return Path.of(dir);
	}
}
