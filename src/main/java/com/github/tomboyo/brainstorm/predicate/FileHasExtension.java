package com.github.tomboyo.brainstorm.predicate;

import java.io.File;

import com.github.tomboyo.brainstorm.configuration.PropertyConfig;
import com.github.tomboyo.brainstorm.util.FileUtil;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileHasExtension implements Predicate {
	@Autowired
	private PropertyConfig config;

	@Override
	public boolean matches(Exchange exchange) {
		var file = exchange.getIn().getBody(File.class);
		return config.notebookFileExtensions().contains(
			FileUtil.getExtension(file));
	}
}