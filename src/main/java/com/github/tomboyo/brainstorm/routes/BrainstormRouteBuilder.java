package com.github.tomboyo.brainstorm.routes;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class BrainstormRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("seda:file.event");
    }

    @Bean
    public ProducerTemplate fileEventProducer() {
        var producer = getContext().createProducerTemplate();
        producer.setDefaultEndpointUri("seda:file.event");
        return producer;
    }
}