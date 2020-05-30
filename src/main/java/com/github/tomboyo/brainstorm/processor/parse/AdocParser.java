package com.github.tomboyo.brainstorm.processor.parse;

import java.nio.file.Path;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.tomboyo.brainstorm.graph.model.Reference;

public class AdocParser {
	private static final Pattern reference = compileReferencePattern();

	private static final Pattern compileReferencePattern() {
		var file = "([^#]+)";
		var section = "[^,]+";
		var displayText = "([^>]+)";
		var patternText =
			"<<" + file + "#" + section + "," + displayText + ">>";
		return Pattern.compile(patternText);
	}

	public static Set<Reference> parseReferences(
		Path source,
		String document
	) {
		return reference.matcher(document).results()
			.map(result -> toReference(source, result))
			.collect(Collectors.toSet());
	}

	private static Reference toReference(
		Path source,
		MatchResult result
	) {
		var destination = source.getParent().resolve(result.group(1).trim());
		var context = result.group(2).trim();
		
		return new Reference(source, destination, context);
	}
}