package com.github.tomboyo.brainstorm.processor;

import java.io.File;
import java.nio.file.Files;

import com.github.tomboyo.brainstorm.processor.parse.AdocParser;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class DocumentReferenceProcessor implements Processor {
	@Override
	public void process(Exchange exchange) throws Exception {
		var path = exchange.getIn().getBody(File.class).toPath();
		var document = new String(Files.readAllBytes(path), "utf8");
		var references = AdocParser.parseReferences(path, document);

		exchange.getMessage().setBody(references);
	}
}