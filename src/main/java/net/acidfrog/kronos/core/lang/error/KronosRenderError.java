package net.acidfrog.kronos.core.lang.error;

import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary.KronosErrorMessage;

public final class KronosRenderError extends KronosError {
    private static final long serialVersionUID = 1L;

    public KronosRenderError(KronosErrorMessage error) {
        super(error);
    }

}
