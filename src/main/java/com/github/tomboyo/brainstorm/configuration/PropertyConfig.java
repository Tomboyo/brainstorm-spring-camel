package com.github.tomboyo.brainstorm.configuration;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("${configuration.file}")
public class PropertyConfig {
	private final Path dataDirectory;
	private final Path notebookDirectory;

	@Autowired
	public PropertyConfig(
		@Value("${data.directory}") String dataDir,
		@Value("${notebook.directory}") String notebookDir
	) {
		this.dataDirectory = Path.of(dataDir);
		this.notebookDirectory = Path.of(notebookDir);
	}

	public Path dataDirectory() {
		return dataDirectory;
	}

	public Path notebookDirectory() {
		return notebookDirectory;
	}
}
