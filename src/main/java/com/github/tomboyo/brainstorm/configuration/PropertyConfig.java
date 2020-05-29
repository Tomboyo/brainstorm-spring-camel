package com.github.tomboyo.brainstorm.configuration;

import java.nio.file.Path;
import java.util.Set;

import com.github.tomboyo.brainstorm.util.PropertyUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("${configuration.file}")
@Profile("!test")
public class PropertyConfig {
	private final Path notebookDirectory;
	private final Set<String> notebookFileExtensions;

	@Autowired
	public PropertyConfig(
		@Value("${notebook.directory}") String directory,
		@Value("${notebook.file.extensions}") String extensions
	) {
		this.notebookDirectory = Path.of(directory);
		this.notebookFileExtensions =
			PropertyUtil.parseNotebookFileExtensions(extensions);
	}

	public Path notebookDirectory() {
		return notebookDirectory;
	}

	public Set<String> notebookFileExtensions() {
		return notebookFileExtensions;
	}
}
