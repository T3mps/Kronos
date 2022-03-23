
package net.acidfrog.kronos.core.lang.error;

import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary.KronosErrorMessage;

/**
 * Implementation of an {@link Error} for Kronos Physics.
 * 
 * @author Ethan Temprovich
 */
public final class KronosGeometryError extends KronosError {
	private static final long serialVersionUID = 1L;

	public KronosGeometryError(KronosErrorMessage error) {
		super(error);
	}

}
