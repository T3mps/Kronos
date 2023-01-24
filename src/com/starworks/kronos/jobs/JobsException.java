package com.starworks.kronos.jobs;

import com.starworks.kronos.exception.KronosRuntimeException;

public class JobsException extends KronosRuntimeException {
	private static final long serialVersionUID = 1L;

	public JobsException() {
		super();
	}

	public JobsException(String message) {
		super(message);
	}

	public JobsException(String message, Throwable cause) {
		super(message, cause);
	}

	public JobsException(Throwable cause) {
		super(cause);
	}
}
