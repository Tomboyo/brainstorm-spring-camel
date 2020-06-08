package com.github.tomboyo.brainstorm.graph.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

/**
 * Directed reference to a Document; the "source" of the reference is
 * contextual.
 */
@JsonAutoDetect(getterVisibility = Visibility.PROTECTED_AND_PUBLIC)
public record Reference(
	String context,
	Document destination
) {
	/** For Jackson deserialization only. */
	@JsonCreator
	public static Reference jsonCreator(
		@JsonProperty("context") String context,
		@JsonProperty("destination") Document destination
	) {
		return new Reference(context, destination);
	}

	/** For Jackson serialization only. */
	protected String getContext() {
		return context;
	}

	/** For Jackson serialization only. */
	protected Document getDestination() {
		return destination;
	}
}
