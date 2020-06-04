package com.github.tomboyo.brainstorm.graph.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(getterVisibility = Visibility.PROTECTED_AND_PUBLIC)
public record Graph(
	Document document,
	Set<Reference> outboundReferences,
	Set<Reference> inboundReferences
) {
	/* For Jackson serialization only. */
	protected Document getDocument() {
		return document;
	}

	/* For Jackson serialization only. */
	protected Set<Reference> getInboundReferences() {
		return inboundReferences;
	}

	/* For Jackson serialization only. */
	protected Set<Reference> getOutboundReferences() {
		return outboundReferences;
	}
}
