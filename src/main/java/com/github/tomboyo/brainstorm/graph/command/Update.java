package com.github.tomboyo.brainstorm.graph.command;

import java.util.Set;

import com.github.tomboyo.brainstorm.graph.model.Document;
import com.github.tomboyo.brainstorm.graph.model.Reference;

public record Update(
	Document source,
	Set<Reference> outboundReferences
) {}
