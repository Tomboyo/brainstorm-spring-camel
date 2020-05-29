package com.github.tomboyo.brainstorm.configuration;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamelConfig {

	@Bean
	public CamelContext camelContext() {
		var context = new DefaultCamelContext();
		context.start();
		return context;
	}

	/**
	 * Create a new ProducerTemplate for the file.event endpoint. The instance
	 * is closed automatically.
	 */
	@Bean
	public ProducerTemplate fileEventProducerTemplate(
		CamelContext context
	) {
		var template = context.createProducerTemplate();
		template.setDefaultEndpointUri("seda:file.event");
		return template;
	}

	@Bean
	public ProducerTemplate mockFileEventProducerTemplate(
		CamelContext context
	) {
		var template = context.createProducerTemplate();
		template.setDefaultEndpointUri("mock:file.event");
		return template;
	}

	@Bean
	public MockEndpoint mockFileEventEndpoint(
		CamelContext context
	) {
		return context.getEndpoint("mock:file.event", MockEndpoint.class);
	}
}
