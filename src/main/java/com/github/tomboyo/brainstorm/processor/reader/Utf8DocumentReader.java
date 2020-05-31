package com.github.tomboyo.brainstorm.processor.reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.tomboyo.brainstorm.processor.reader.DocumentReader;

import org.springframework.stereotype.Component;

@Component
public class Utf8DocumentReader implements DocumentReader {
	@Override
	public String read(Path path) throws IOException {
		return new String(Files.readAllBytes(path), "utf8");
	}
}