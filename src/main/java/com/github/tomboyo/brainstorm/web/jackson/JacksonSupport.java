package com.github.tomboyo.brainstorm.web.jackson;

import java.io.IOException;
import java.nio.file.Path;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonSupport {
	private static final String groupId = "com.github.tomboyo.brainstorm";
	private static final String artifactId = "brainstorm";

	@Bean @Primary
	public static ObjectMapper defaultObjectMapper() {
		var mapper = new ObjectMapper();
		mapper.registerModule(defaultModule());
		return mapper;
	}

	private static Module defaultModule() {
		var module = new SimpleModule(
			"DefaultModule",
			new Version(1, 0, 0, null, groupId, artifactId));
		module.addSerializer(Path.class, new PathSerializer());
		return module;
	}

	private static class PathSerializer extends StdSerializer<Path> {
		private static final long serialVersionUID = 1L;
		
		private PathSerializer() {
			super(Path.class);
		}

		@Override
		public void serialize(
			Path value,
			JsonGenerator gen,
			SerializerProvider provider
		) throws IOException {
			gen.writeString(value.normalize().toString());
		}
	}
}