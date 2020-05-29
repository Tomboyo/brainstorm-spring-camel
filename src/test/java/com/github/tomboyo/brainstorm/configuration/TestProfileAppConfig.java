package com.github.tomboyo.brainstorm.configuration;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@Profile("test")
public class TestProfileAppConfig {
	@Bean
	public ThreadPoolTaskExecutor defaultExecutor() {
		return new ThreadPoolTaskExecutor();
	}
}