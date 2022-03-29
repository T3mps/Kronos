package net.acidfrog.kronos.job.tasks;

import net.acidfrog.kronos.core.lang.annotations.Internal;
import net.acidfrog.kronos.core.lang.logger.Logger;

public @Internal interface CloseTask extends Task {

    @Override
    public default void execute() {
        Logger.instance.logInfo("Job system closed.");
    }

}
