package com.github.tomboyo.brainstorm.graph.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public record Document(
	Path location
) {
	public Map<String, Object> toMap() {
		var map = new HashMap<String, Object>();
		map.put("location", location.toString());
		return map;
	}

	public static Document fromString(String location) {
		return new Document(Paths.get(location));
	}
}
