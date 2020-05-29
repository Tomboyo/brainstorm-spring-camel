package com.github.tomboyo.brainstorm.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class TestProfileRouteBuilder {
	@Bean
	public CamelContext defaultCamelContext() throws Exception {
		var context = new DefaultCamelContext();

		context.addRoutes(new RouteBuilder(){
			@Override
			public void configure() throws Exception {
				from("direct:file.event")
					.to("log:debug")
					.to("mock:file.event");
			}
		});

		context.start();
		return context;
	}

	@Bean
	public ProducerTemplate fileEventProducer(
		CamelContext context
	) {
		var producer = context.createProducerTemplate();
		producer.setDefaultEndpointUri("direct:file.event");
		return producer;
	}

	@Bean
	public MockEndpoint fileEventEndpoint(
		CamelContext context
	) {
		return (MockEndpoint) context.getEndpoint("mock:file.event");
	}
}