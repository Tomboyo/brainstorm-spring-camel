package com.github.tomboyo.brainstorm.graph.model;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(getterVisibility = Visibility.PROTECTED_AND_PUBLIC)
public record Document(
	URI uri
) {
	/* For Jackson serialization only. */
	protected URI getUri() {
		return uri;
	}
}
