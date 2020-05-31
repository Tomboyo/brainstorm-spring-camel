package com.github.tomboyo.brainstorm.processor.reader;

import java.io.IOException;
import java.nio.file.Path;

@FunctionalInterface
public interface DocumentReader {
	String read(Path path) throws IOException;
}
