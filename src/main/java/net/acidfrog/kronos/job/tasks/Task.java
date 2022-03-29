package net.acidfrog.kronos.job.tasks;

import net.acidfrog.kronos.core.lang.annotations.Internal;

public @Internal interface Task {
    
    public void execute();

}
