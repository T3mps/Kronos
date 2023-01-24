package com.starworks.kronos.exception;

public class KronosException extends Exception {
	private static final long serialVersionUID = 1L;

	public KronosException() {
		super();
	}

	public KronosException(String message) {
		super(message);
	}

	public KronosException(String message, Throwable cause) {
		super(message, cause);
	}

	public KronosException(Throwable cause) {
		super(cause);
	}
}
