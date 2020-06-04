package com.github.tomboyo.brainstorm.predicate;

import java.io.File;

import com.github.tomboyo.brainstorm.util.FileUtil;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.springframework.stereotype.Component;

@Component
public class IsAdocFile implements Predicate {
	@Override
	public boolean matches(Exchange exchange) {
		return FileUtil.getExtension(
				exchange.getIn().getBody(File.class))
			.toLowerCase()
			.equals("adoc");
	}
}