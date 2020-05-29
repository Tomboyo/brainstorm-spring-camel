package com.github.tomboyo.brainstorm.configuration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.tomboyo.brainstorm.util.PropertyUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;

@Configuration
@Profile("test")
public class TestProfilePropertyConfig {
	@Bean @Scope("prototype")
	public static TemporaryDirectory tempDir() throws IOException {
		return new TemporaryDirectory("test-");
	}

	@Bean @Scope("prototype")
	public static Path notebookDirectory(
		TemporaryDirectory tmpDir
	) {
		return tmpDir.getPath();
	}

	@Bean
	public static Set<String> notebookFileExtensions(
		@Value("${notebook.file.extensions}") String extensions
	) {
		return PropertyUtil.parseNotebookFileExtensions(extensions);
	}
}