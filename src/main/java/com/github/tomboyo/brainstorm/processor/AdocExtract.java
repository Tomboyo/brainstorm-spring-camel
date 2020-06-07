package com.github.tomboyo.brainstorm.processor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.tomboyo.brainstorm.graph.command.Update;
import com.github.tomboyo.brainstorm.graph.model.Document;
import com.github.tomboyo.brainstorm.graph.model.Reference;

import org.springframework.stereotype.Component;

@Component
public class AdocExtract {
	private static final Pattern referencePattern = newReferencePattern();

	private static final Pattern newReferencePattern() {
		var file = "([^#]+)";
		var section = "[^,]+";
		var displayText = "([^>]+)";
		var pattern =
			"<<" + file + "#" + section + "," + displayText + ">>";
		return Pattern.compile(pattern);
	}

	/**
	 * Given an adoc file, extract its references into an Update.
	 */
	public static Update extractUpdate(Path path) throws IOException {
		var contents = new String(Files.readAllBytes(path), "utf8");
		return createUpdateCommand(path, contents);
	}

	private static Update createUpdateCommand(
		Path updated,
		String contents
	) throws IOException {
		return new Update(
			new Document(updated.toUri()),
			referencePattern.matcher(contents).results()
				.map(result -> createReference(updated, result))
				.collect(Collectors.toSet()));
	}

	private static Reference createReference(Path updated, MatchResult result) {
		var destination = updated.getParent().resolve(
			result.group(1).trim());
		var context = result.group(2).trim();

		return new Reference(
			context,
			new Document(withImpliedExtension(destination).toUri()));
	}

	/**
	 * Adoc references do not have to specify full file names:
	 * <<foo#...>> is assumed to mean <<foo.adoc#...>>.
	 * This makes sure paths contain the extension.
	 */
	private static Path withImpliedExtension(Path original) {
		if (!original.toString().endsWith(".adoc")) {
			return Paths.get(original.toString() + ".adoc");
		} else {
			return original;
		}
	}
}