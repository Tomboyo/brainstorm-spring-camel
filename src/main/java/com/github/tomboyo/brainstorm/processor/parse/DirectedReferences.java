package com.github.tomboyo.brainstorm.processor.parse;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DirectedReferences {
	private final Path source;
	private final Set<ReferenceDestination> destinations;

	public DirectedReferences(
		Path source,
		Set<ReferenceDestination> to
	) {
		this.source = source;
		this.destinations = to.stream().collect(Collectors.toUnmodifiableSet());
	}

	public Path getSource() {
		return this.source;
	}

	public Set<ReferenceDestination> getDestinations() {
		return this.destinations;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof DirectedReferences)) {
			return false;
		}
		DirectedReferences directedReferences = (DirectedReferences) o;
		return Objects.equals(source, directedReferences.source)
			&& Objects.equals(destinations, directedReferences.destinations);
	}

	@Override
	public int hashCode() {
		return Objects.hash(source, destinations);
	}

	@Override
	public String toString() {
		return "{" +
			" source='" + getSource() + "'" +
			", destinations='" + getDestinations() + "'" +
			"}";
	}
	
}