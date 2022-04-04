package net.acidfrog.kronos.core.util.job.tasks;

import net.acidfrog.kronos.core.lang.annotations.Internal;
import net.acidfrog.kronos.core.lang.logger.Logger;

public @Internal interface CloseTask extends Task {

    @Override
    public default void execute() {
        Logger.logInfo("Job system closed.");
    }

}
