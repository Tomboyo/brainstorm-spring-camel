package com.github.tomboyo.brainstorm.configuration;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("${spring.config.location}")
public class PropertyConfig {
	@Bean
	public static Path notebookDirectory(
		@Value("${notebook.directory}") String directory
	) {
		requireNonNull(directory, "notebook.directory must not be null");
		return Path.of(directory);
	}

	@Bean
	public static Set<String> notebookFileExtensions(
		@Value("${notebook.file.extensions}") String extensions
	) {
		return Stream.of(extensions.split("[\\s,]+"))
			.map(ext -> withoutLeadingDot(ext))
			.collect(Collectors.toUnmodifiableSet());
	}

	private static String withoutLeadingDot(String extension) {
		if (extension.startsWith(".")) {
			return extension.substring(1);
		} else {
			return extension;
		}
	}
}
