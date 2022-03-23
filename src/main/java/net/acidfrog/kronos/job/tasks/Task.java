package net.acidfrog.kronos.job.tasks;

@FunctionalInterface
public interface Task {

    public void perform();
    
}
