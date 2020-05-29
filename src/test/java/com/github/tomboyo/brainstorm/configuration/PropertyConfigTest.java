package com.github.tomboyo.brainstorm.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PropertyConfigTest {
	@Test
	public void testExtensions() {
		assertEquals(
			Set.of("x", "y", "z"),
			PropertyConfig.notebookFileExtensions("x y z"),
			"Extensions may be space delimited");
		assertEquals(
			Set.of("x", "y", "z"),
			PropertyConfig.notebookFileExtensions("x,y,z"),
			"Extensions may be comma delimited");
		assertEquals(
			Set.of("x", "y", "z"),
			PropertyConfig.notebookFileExtensions("x, y, z"),
			"Extensions may be space and comma delimited");
		assertEquals(
			Set.of("x.y", "z"),
			PropertyConfig.notebookFileExtensions(".x.y, .z"),
			"The leading . of extensions are removed");
	}
}
