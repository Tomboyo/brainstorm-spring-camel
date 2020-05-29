package com.github.tomboyo.brainstorm.util;

import java.io.File;

public final class FileUtil {
	public static String getExtension(File file) {
		var name = file.getName();
		var dotIndex = name.lastIndexOf(".");
		if (dotIndex == -1) {
			return "";
		} else {
			return name.substring(dotIndex + 1);
		}
	}
}