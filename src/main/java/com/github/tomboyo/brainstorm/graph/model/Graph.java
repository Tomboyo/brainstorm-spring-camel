package com.github.tomboyo.brainstorm.graph.model;

import java.util.Set;

public record Graph(
	Document source,
	Set<Reference> references
) {}
