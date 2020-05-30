package com.github.tomboyo.brainstorm.graph.model;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public record Reference(
	Path source,
	Path destination,
	String context
) {
	public Map<String, Object> toMap() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("source", source.toString());
		map.put("destination", destination.toString());
		map.put("context", context);
		return map;
	}
}
