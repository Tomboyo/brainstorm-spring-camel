package com.github.tomboyo.brainstorm.graph.model;

import java.util.HashMap;
import java.util.Map;

public record Reference(
	Document source,
	Document destination,
	String context
) {
	public Map<String, Object> toMap() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("source", source.toMap());
		map.put("destination", destination.toMap());
		map.put("context", context);
		return map;
	}
}
