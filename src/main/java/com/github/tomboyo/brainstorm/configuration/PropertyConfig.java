package com.github.tomboyo.brainstorm.configuration;

import java.nio.file.Path;
import java.util.Set;

import com.github.tomboyo.brainstorm.util.PropertyUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("${configuration.file}")
public class PropertyConfig {
	private final Path dataDirectory;
	private final Path notebookDirectory;
	private final Set<String> notebookFileExtensions;

	@Autowired
	public PropertyConfig(
		@Value("${data.directory}") String dataDir,
		@Value("${notebook.directory}") String notebookDir,
		@Value("${notebook.file.extensions}") String extensions
	) {
		this.dataDirectory = Path.of(dataDir);
		this.notebookDirectory = Path.of(notebookDir);
		this.notebookFileExtensions =
			PropertyUtil.parseNotebookFileExtensions(extensions);
	}

	public Path dataDirectory() {
		return dataDirectory;
	}

	public Path notebookDirectory() {
		return notebookDirectory;
	}

	public Set<String> notebookFileExtensions() {
		return notebookFileExtensions;
	}
}
