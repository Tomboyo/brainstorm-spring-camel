package com.github.tomboyo.brainstorm.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;
import java.util.Set;

import com.github.tomboyo.brainstorm.graph.command.Update;
import com.github.tomboyo.brainstorm.graph.model.Document;
import com.github.tomboyo.brainstorm.graph.model.Reference;

import org.junit.jupiter.api.Test;


public class AdocDocumentProcessorTest {

	@Test
	public void createCommand() throws Exception {
		var subject = new AdocDocumentUpdateProcessor(
			(_path) ->
				"""
				foo <<foo.adoc#foosection,footext>> bar
				foo bar baz
				foo <<bar.adoc#barsection,bartext>> bar
				""");

		var actual = subject.createCommand(Paths.get("/foo/bar/baz.adoc"));

		var source = new Document(Paths.get("/foo/bar/baz.adoc"));
		var expected = new Update(
			source,
			Set.of(
				new Reference(
					source,
					new Document(Paths.get("/foo/bar/foo.adoc")),
					"footext"),
				new Reference(
					source,
					new Document(Paths.get("/foo/bar/bar.adoc")),
					"bartext")));
		
		assertEquals(expected, actual);
	}

	@Test
	public void references_ignoreWhitespace() throws Exception {
		var subject = new AdocDocumentUpdateProcessor(
			(_path) -> "<< \n\tfoo.adoc \n\t# \n\tfoosection \n\t, \n\tfootext \n\t>>");
		
		var actual = subject.createCommand(Paths.get("/foo/bar/baz.adoc"));

		var source = new Document(Paths.get("/foo/bar/baz.adoc"));
		var expected = new Update(
			source,
			Set.of(
				new Reference(
					source,
					new Document(Paths.get("/foo/bar/foo.adoc")),
					"footext")));
		
		assertEquals(expected, actual);
	}

	@Test
	public void references_addsImpliedExtensions() throws Exception {
		var subject = new AdocDocumentUpdateProcessor(
			// The reference is to the extensionless `foo`
			(_path) -> "<<foo#section,displaytext>>");
		
		var actual = subject.createCommand(Paths.get("/foo/bar/source.adoc"));

		var source = new Document(Paths.get("/foo/bar/source.adoc"));
		var expected = new Update(
			source,
			Set.of(
				new Reference(
					source,
					// The destination assumes the `.adoc` extension
					new Document(Paths.get("/foo/bar/foo.adoc")),
					"displaytext")));
		
		assertEquals(expected, actual);
	}
}