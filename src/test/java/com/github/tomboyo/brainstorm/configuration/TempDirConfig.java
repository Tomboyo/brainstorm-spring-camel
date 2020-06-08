package com.github.tomboyo.brainstorm.configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("tempDirs")
public class TempDirConfig {
	private final Path data;
	private final Path notebook;

	public TempDirConfig() throws IOException {
		data = Files.createTempDirectory("testdata-");
		notebook = Files.createTempDirectory("testnotebook-");
	}

	@Bean("data.directory")
	public File dataDirectory() {
		return data.toFile();
	}

	@Bean("notebook.directory")
	public Path notebookDirectory() {
		return notebook;
	}

	public void close() throws IOException {
		Files.delete(data);
		Files.delete(notebook);
	}
}