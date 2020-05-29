package com.github.tomboyo.brainstorm.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;

public final class PropertyUtilTest {
	@Test
	public void testExtensions() {
		assertEquals(
			Set.of("x", "y", "z"),
			PropertyUtil.parseNotebookFileExtensions("x y z"),
			"Extensions may be space delimited");
		assertEquals(
			Set.of("x", "y", "z"),
			PropertyUtil.parseNotebookFileExtensions("x,y,z"),
			"Extensions may be comma delimited");
		assertEquals(
			Set.of("x", "y", "z"),
			PropertyUtil.parseNotebookFileExtensions("x, y, z"),
			"Extensions may be space and comma delimited");
		assertEquals(
			Set.of("x.y", "z"),
			PropertyUtil.parseNotebookFileExtensions(".x.y, .z"),
			"The leading . of extensions are removed");
	}
}