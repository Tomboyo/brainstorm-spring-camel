package com.github.tomboyo.brainstorm.processor.parse;

import java.nio.file.Path;
import java.util.Objects;

public class ReferenceDestination {
	private final Path filePath;
	private final String section;
	private final String displayText;

	public ReferenceDestination(
		Path filePath,
		String section,
		String displayText
	) {
		this.filePath = filePath;
		this.section = section;
		this.displayText = displayText;
	}

	public Path getFilePath() {
		return this.filePath;
	}


	public String getSection() {
		return this.section;
	}


	public String getDisplayText() {
		return this.displayText;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof ReferenceDestination)) {
			return false;
		}
		ReferenceDestination referenceDestination = (ReferenceDestination) o;
		return Objects.equals(filePath, referenceDestination.filePath)
			&& Objects.equals(section, referenceDestination.section)
			&& Objects.equals(displayText, referenceDestination.displayText);
	}

	@Override
	public int hashCode() {
		return Objects.hash(filePath, section, displayText);
	}

	@Override
	public String toString() {
		return "{" +
			" filePath='" + getFilePath() + "'" +
			", section='" + getSection() + "'" +
			", displayText='" + getDisplayText() + "'" +
			"}";
	}

}