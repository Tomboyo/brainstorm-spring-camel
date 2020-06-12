package com.github.tomboyo.brainstorm.web.handler;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class GraphHandlerTest {
	@Test
	public void missingLocation() {
		fail("TODO! no location param");
	}

	@Test
	public void emtpyLocation() {
		fail("TODO! location=");
	}

	@Test
	public void documentNotFound() {
		fail("TODO!");
	}

	@Test
	public void internalServerError() {
		fail("TODO! Should propagate error out");
	}

	@Test
	public void ok() {
		fail("TODO! happy path");
	}
}