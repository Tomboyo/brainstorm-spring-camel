package com.github.tomboyo.brainstorm.util;

import java.util.function.Function;

public final class Functions {

	@FunctionalInterface
	public interface ThrowingFunction<I, O> {
		O apply(I x) throws Exception;
	}

	public static class TunneledException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public TunneledException(Throwable cause) {
			super(cause);
		}
	}

	public static <I, O> Function<I, O> tunneledFunction(
		ThrowingFunction<I, O> function
	) {
		return (I x) -> {
			try {
				return function.apply(x);
			} catch (Exception e) {
				throw new TunneledException(e);
			}
		};
	}
}