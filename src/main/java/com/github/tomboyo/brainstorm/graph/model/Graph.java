package com.github.tomboyo.brainstorm.graph.model;

import java.util.Set;

public record Graph(
	Document document,
	Set<Reference> outboundReferences,
	Set<Reference> inboundReferences
) {}
