package com.starworks.kronos.exception;

public class KronosRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public KronosRuntimeException() {
		super();
	}

	public KronosRuntimeException(String message) {
		super(message);
	}

	public KronosRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public KronosRuntimeException(Throwable cause) {
		super(cause);
	}
}
