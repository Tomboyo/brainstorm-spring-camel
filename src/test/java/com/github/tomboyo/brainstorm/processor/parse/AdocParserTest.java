package com.github.tomboyo.brainstorm.processor.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import com.github.tomboyo.brainstorm.graph.model.Reference;

import org.junit.jupiter.api.Test;

public class AdocParserTest {
	@Test
	public void references() {
		Path source = Paths.get("foo/bar/baz.adoc");
		String document =
			"<<foo.adoc#foosection,footext>>\n"
			+ "this is another line of the file\n"
			+ "another line <<bar.adoc#barsection,bartext>>\n";
		
		var expected = Set.of(
			new Reference(
				source, Paths.get("foo/bar/foo.adoc"), "footext"),
			new Reference(
				source, Paths.get("foo/bar/bar.adoc"), "bartext"));
		
		assertEquals(expected, AdocParser.parseReferences(source, document));
	}

	@Test
	public void references_ignoreWhitespace() {
		Path source = Paths.get("foo/bar/baz.adoc");
		String document =
			"<< \n\tfoo.adoc \n\t# \n\tfoosection \n\t, \n\tfootext \n\t>>";
		
		var expected = Set.of(
			new Reference(
				source, Paths.get("foo/bar/foo.adoc"), "footext"));
		
		assertEquals(expected, AdocParser.parseReferences(source, document));
	}

	@Test
	public void references_addsImpliedExtensions() {
		// <<foo#section,text>> has the _implied_ .adoc extension
		fail("TODO!");
	}
}