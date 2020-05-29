package com.github.tomboyo.brainstorm.util;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PropertyUtil {

	public static Set<String> parseNotebookFileExtensions(
		String extensions
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