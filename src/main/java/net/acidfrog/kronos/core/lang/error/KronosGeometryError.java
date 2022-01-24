
package net.acidfrog.kronos.core.lang.error;

import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary.KronosError;

/**
 * Implementation of an {@link Error} for Kronos Physics.
 * 
 * @author Ethan Temprovich
 */
public class KronosGeometryError extends Error {

	private static final long serialVersionUID = 1L;

	public KronosGeometryError(KronosError error) { super(error.message()); }

}
