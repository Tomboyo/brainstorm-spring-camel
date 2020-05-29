package com.github.tomboyo.brainstorm.processor.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class AdocParserTest {
	@Test
	public void references() {
		Path source = Paths.get("foo/bar/baz.adoc");
		String document =
			"<<foo.adoc#foosection,footext>>\n"
			+ "this is another line of the file\n"
			+ "another line <<bar.adoc#barsection,bartext>>\n";
		
		var expected = new DirectedReferences(
			source,
			Set.of(
				new ReferenceDestination(
					Paths.get("foo/bar/foo.adoc"), "foosection", "footext"),
				new ReferenceDestination(
					Paths.get("foo/bar/bar.adoc"), "barsection", "bartext")));
		
		assertEquals(expected, AdocParser.parseReferences(source, document));
	}

	@Test
	public void references_ignoreWhitespace() {
		Path source = Paths.get("foo/bar/baz.adoc");
		String document =
			"<< \n\tfoo.adoc \n\t# \n\tfoosection \n\t, \n\tfootext \n\t>>";
		
		var expected = new DirectedReferences(
			source,
			Set.of(
				new ReferenceDestination(
					Paths.get("foo/bar/foo.adoc"), "foosection", "footext")));
		
		assertEquals(expected, AdocParser.parseReferences(source, document));
	}
}