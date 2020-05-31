package com.github.tomboyo.brainstorm.graph.command;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public record Query(
	Path location
) {
	public static Query fromString(String location) {
		return new Query(Paths.get(location));
	}

	public Map<String, Object> toMap() {
		var map = new HashMap<String, Object>();
		map.put("location", location.toString());
		return map;
	}
}
