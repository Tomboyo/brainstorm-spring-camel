package com.github.tomboyo.brainstorm.graph.model;

import java.nio.file.Path;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(getterVisibility = Visibility.PROTECTED_AND_PUBLIC)
public record Document(
	Path location
) {
	/** For Jackson deserialization only. */
	@JsonCreator
	protected static Document jsonCreator(
		@JsonProperty("location") Path location
	) {
		return new Document(location);
	}

	/** For Jackson serialization only. */
	protected Path getLocation() {
		return location;
	}
}
