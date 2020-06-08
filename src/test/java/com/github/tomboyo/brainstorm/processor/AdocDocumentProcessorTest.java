package com.github.tomboyo.brainstorm.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import com.github.tomboyo.brainstorm.graph.command.Update;
import com.github.tomboyo.brainstorm.graph.model.Document;
import com.github.tomboyo.brainstorm.graph.model.Reference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class AdocDocumentProcessorTest {
	@TempDir Path notebookDir;

	/**
	 * Happy-path: Given a File whose contents contain adoc references to other
	 * adoc documents, create an Update containing all such references from the
	 * file.
	 */
	@Test
	public void extractUpdate() throws Exception {
		var document = notebookDir.resolve("foo.adoc");
		Files.writeString(document, "lorem <<bar.adoc#section,context>> ipsum");

		var actual = AdocExtract.extractUpdate(document);

		var expected = new Update(
			new Document(document),
			Set.of(
				new Reference(
					"context",
					new Document(notebookDir.resolve("bar.adoc")))));
		
		assertEquals(actual, expected);
	}

	/**
	 * Ensure references are parsed despite presence of white space.
	 */
	@Test
	public void extractUpdate_ignoreWhitespace() throws Exception {
		var document = notebookDir.resolve("foo.adoc");
		Files.writeString(document,
			"\n\t <<\n\t bar.adoc\n\t #\n\t section\n\t ,\n\t context\n\t >>\n\t ");

		var actual = AdocExtract.extractUpdate(document);

		var expected = new Update(
			new Document(document),
			Set.of(
				new Reference(
					"context",
					new Document(notebookDir.resolve("bar.adoc")))));
		
		assertEquals(actual, expected);
	}

	/**
	 * Adoc references can omit the .adoc extension, and so the following are
	 * equivalent:
	 * 
	 * <<bar.adoc#...>>
	 * <<bar#...>>
	 * 
	 * Regardless of which is used, all Refernce locations must contain the adoc
	 * extension.
	 */
	@Test
	public void extractUpdate_addsImpliedExtensions() throws Exception {
		var document = notebookDir.resolve("foo.adoc");
		Files.writeString(document, "<<bar#section,context>>");

		var actual = AdocExtract.extractUpdate(document);

		var expected = new Update(
			new Document(document),
			Set.of(
				new Reference(
					"context",
					new Document(notebookDir.resolve("bar.adoc")))));
		
		assertEquals(actual, expected);
	}
}