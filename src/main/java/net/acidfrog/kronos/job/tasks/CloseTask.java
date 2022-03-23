package net.acidfrog.kronos.job.tasks;

import net.acidfrog.kronos.core.lang.logger.Logger;

public interface CloseTask extends Task {

    @Override
    public default void perform() {
        Logger.instance.logInfo("Job system closed.");
    }

}
