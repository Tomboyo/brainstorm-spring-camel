package com.github.tomboyo.brainstorm.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;

import org.junit.jupiter.api.Test;

public final class FileUtilTest {

	@Test
	public void getExtension() {
		var file = new File("/foo/bar/baz.ext");
		assertEquals("ext", FileUtil.getExtension(file));
	}

	@Test
	public void getExtention_whenThereIsNone() {
		var file = new File("/foo/bar/baz");
		assertEquals("", FileUtil.getExtension(file));
	}

	@Test
	public void getExtension_whenSeveralExtensions() {
		var file = new File("/foo/bar/baz.ext.last");
		assertEquals("last", FileUtil.getExtension(file));
	}
}