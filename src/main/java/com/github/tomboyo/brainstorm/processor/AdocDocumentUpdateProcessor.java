package com.github.tomboyo.brainstorm.processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.tomboyo.brainstorm.graph.command.Update;
import com.github.tomboyo.brainstorm.graph.model.Document;
import com.github.tomboyo.brainstorm.graph.model.Reference;
import com.github.tomboyo.brainstorm.processor.reader.DocumentReader;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdocDocumentUpdateProcessor implements Processor {
	private static final Pattern referencePattern = newReferencePattern();

	private final DocumentReader reader;

	@Autowired
	public AdocDocumentUpdateProcessor(
		DocumentReader reader
	) {
		this.reader = reader;
	}

	private static final Pattern newReferencePattern() {
		var file = "([^#]+)";
		var section = "[^,]+";
		var displayText = "([^>]+)";
		var pattern =
			"<<" + file + "#" + section + "," + displayText + ">>";
		return Pattern.compile(pattern);
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		var body = exchange.getIn().getBody(File.class);
		var command = createCommand(body.toPath());
		exchange.getMessage().setBody(command);
	}

	/**
	 * Create an Update command based on the contents of a given File.
	 */
	public Update createCommand(Path path) throws IOException {
		var source = new Document(path);
		var references = parseReferences(source, reader.read(path));
		return new Update(source, references);
	}

	private static Set<Reference> parseReferences(
		Document source,
		String documentContents
	) {
		return referencePattern.matcher(documentContents).results()
			.map(result -> {
				var destinationPath = source.location().getParent()
					.resolve(result.group(1).trim());
				destinationPath = withImpliedExtension(destinationPath);
				var destination = new Document(destinationPath);
				return new Reference(source, destination, result.group(2).trim());
			})
			.collect(Collectors.toSet());
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