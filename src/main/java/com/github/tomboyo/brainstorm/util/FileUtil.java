package com.github.tomboyo.brainstorm.util;

import java.nio.file.Path;
import java.util.function.Function;

public final class FileUtil {
	// Not a Predicate because of Camel API.
	public static Function<Path, Object> hasExtension(String extension) {
		return (path) -> extension.equals(getExtension(path));
	}

	public static String getExtension(Path path) {
		var name = path.getFileName().toString();
		var dotIndex = name.lastIndexOf(".");
		if (dotIndex == -1) {
			return "";
		} else {
			return name.substring(dotIndex + 1);
		}
	}
}