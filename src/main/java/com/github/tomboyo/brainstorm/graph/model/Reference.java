package com.github.tomboyo.brainstorm.graph.model;

/**
 * Directed reference to a Document; the "source" of the reference is
 * contextual.
 */
public record Reference(
	String context,
	Document destination
) {}
