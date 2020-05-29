package com.github.tomboyo.brainstorm.processor.parse;

import java.nio.file.Path;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AdocParser {
	private static final Pattern reference = compileReferencePattern();

	private static final Pattern compileReferencePattern() {
		var file = "([^#]+)";
		var section = "([^,]+)";
		var displayText = "([^>]+)";
		var patternText =
			"<<" + file + "#" + section + "," + displayText + ">>";
		return Pattern.compile(patternText);
	}

	public static DirectedReferences parseReferences(
		Path source,
		String document
	) {
		var destinations = reference.matcher(document).results()
			.map(result -> toDestination(source, result))
			.collect(Collectors.toSet());
		return new DirectedReferences(source, destinations);
	}

	private static ReferenceDestination toDestination(
		Path source,
		MatchResult result
	) {
		var filePath = source.getParent().resolve(result.group(1).trim());
		var section = result.group(2).trim();
		var displayText = result.group(3).trim();
		
		return new ReferenceDestination(
			filePath, section, displayText);
	}
}