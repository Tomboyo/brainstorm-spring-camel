package com.github.tomboyo.brainstorm.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AppConfig {
	@Bean
	public ThreadPoolTaskExecutor defaultExecutor() {
		return new ThreadPoolTaskExecutor();
	}
}
