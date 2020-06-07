package com.github.tomboyo.brainstorm.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

public final class FileUtilTest {

	@Test
	public void getExtension() {
		var path = Paths.get("/foo/bar/baz.ext");
		assertEquals("ext", FileUtil.getExtension(path));
	}

	@Test
	public void getExtention_whenThereIsNone() {
		var path = Paths.get("/foo/bar/baz");
		assertEquals("", FileUtil.getExtension(path));
	}

	@Test
	public void getExtension_whenSeveralExtensions() {
		var path = Paths.get("/foo/bar/baz.ext.last");
		assertEquals("last", FileUtil.getExtension(path));
	}
}