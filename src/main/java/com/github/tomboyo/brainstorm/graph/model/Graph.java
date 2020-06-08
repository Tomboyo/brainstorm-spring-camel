package com.github.tomboyo.brainstorm.graph.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(getterVisibility = Visibility.PROTECTED_AND_PUBLIC)
public record Graph(
	Document document,
	Set<Reference> outboundReferences,
	Set<Reference> inboundReferences
) {
	/** For Jackson deserialization only. */
	@JsonCreator
	protected static Graph jsonCreator(
		@JsonProperty("document") Document document,
		@JsonProperty("outboundReferences") Set<Reference> outboundReferences,
		@JsonProperty("inboundReferences") Set<Reference> inboundReferences
	) {
		return new Graph(document, outboundReferences, inboundReferences);
	}

	/** For Jackson serialization only. */
	protected Document getDocument() {
		return document;
	}

	/** For Jackson serialization only. */
	protected Set<Reference> getInboundReferences() {
		return inboundReferences;
	}

	/** For Jackson serialization only. */
	protected Set<Reference> getOutboundReferences() {
		return outboundReferences;
	}
}
