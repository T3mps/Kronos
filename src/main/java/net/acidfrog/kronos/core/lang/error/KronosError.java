package net.acidfrog.kronos.core.lang.error;

import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary.KronosErrorMessage;

/**
 * Implementation of a general {@link Error} for Kronos.
 * 
 * @author Ethan Temprovich
 */
public sealed class KronosError extends Error permits KronosGeometryError {
	private static final long serialVersionUID = 1L;

	public KronosError(KronosErrorMessage error) {
		super(error.message());
	}

	public KronosError(KronosErrorMessage error, String string) {
		super(error.message() + ": " + string);
	}

}
