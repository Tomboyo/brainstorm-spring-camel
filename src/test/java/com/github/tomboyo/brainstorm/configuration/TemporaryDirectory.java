package com.github.tomboyo.brainstorm.configuration;

import static java.util.Collections.emptySet;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemporaryDirectory {
	private static final Logger logger =
		LoggerFactory.getLogger(TemporaryDirectory.class);

	private final Path directory;

	public TemporaryDirectory(
		String prefix,
		FileAttribute<?>... attrs
	) throws IOException {
		this.directory = Files.createTempDirectory(prefix, attrs);
		logger.debug("Created temp dir ({})", directory);
	}

	public Path getPath() {
		return directory;
	}

	public void close() throws IOException {
		logger.debug("Removing temp dir ({})", directory);
		Files.walkFileTree(
			directory, emptySet(), Integer.MAX_VALUE, new DeleteAllFiles());
	}

	private static class DeleteAllFiles extends SimpleFileVisitor<Path> {
		@Override
		public FileVisitResult visitFile(
			Path path,
			BasicFileAttributes attrs
		) throws IOException {
			Files.delete(path);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(
			Path dir,
			IOException exc
		) throws IOException {
			if (exc != null) {
				throw exc;
			}
			Files.delete(dir);
			return FileVisitResult.CONTINUE;
		}
	}
}