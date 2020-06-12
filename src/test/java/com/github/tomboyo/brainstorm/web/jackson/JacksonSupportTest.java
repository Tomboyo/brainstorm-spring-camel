package com.github.tomboyo.brainstorm.web.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

public class JacksonSupportTest {
	private static ObjectMapper mapper =
		JacksonSupport.defaultObjectMapper();

	@Test
	public void absolutePaths() throws Exception {
		assertEquals(
			"\"/this/is/a/path\"",
			mapper.writeValueAsString(Paths.get("/this/is/a/path")));
	}

	@Test
	public void denormalizedPaths() throws Exception {
		assertEquals(
			"\"/this/is/a/path\"",
			mapper.writeValueAsString(Paths.get("/this/./is/../is/a/path")));
	}
}