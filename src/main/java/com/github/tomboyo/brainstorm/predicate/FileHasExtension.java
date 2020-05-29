package com.github.tomboyo.brainstorm.predicate;

import java.io.File;
import java.util.Set;

import com.github.tomboyo.brainstorm.util.FileUtil;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;

public class FileHasExtension implements Predicate {
	private final Set<String> extensions;

	public FileHasExtension(Set<String> extensions) {
		this.extensions = extensions;
	}

	@Override
	public boolean matches(Exchange exchange) {
		var file = exchange.getIn().getBody(File.class);
		return extensions.contains(
			FileUtil.getExtension(file));
	}
}