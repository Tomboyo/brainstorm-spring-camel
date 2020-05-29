package com.github.tomboyo.brainstorm.configuration;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.tomboyo.brainstorm.util.PropertyUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("${spring.config.location}")
@Profile("!test")
public class PropertyConfig {
	@Bean
	public static Path notebookDirectory(
		@Value("${notebook.directory}") String directory
	) {
		return Path.of(directory);
	}

	@Bean
	public static Set<String> notebookFileExtensions(
		@Value("${notebook.file.extensions}") String extensions
	) {
		return PropertyUtil.parseNotebookFileExtensions(extensions);
	}
}
