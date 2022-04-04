package net.acidfrog.kronos.core.architecture;

public interface KronosThread extends Runnable {

    public abstract void start();

    public abstract void stop();

    @Override
    public abstract void run();

    public abstract boolean isRunning();
    
}
