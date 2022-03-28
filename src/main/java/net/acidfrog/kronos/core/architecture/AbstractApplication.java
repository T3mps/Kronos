package net.acidfrog.kronos.core.architecture;

public abstract class AbstractApplication implements Runnable {

    protected KronosState state;
    protected volatile boolean running = false;

    public AbstractApplication() {
        this.state = KronosState.INITIALIZING;
        initialize();
    }

    protected abstract void initialize();

    public void start() {
        if (state != KronosState.INITIALIZING) throw new IllegalStateException("Cannot start application in state " + state);
        state = state.next();
        this.running = true;
        
        // implementation
    }

    @Override
    public void run() {
        if (state != KronosState.STARTING) throw new IllegalStateException("Cannot run application in state " + state);
        state = state.next();

        // implementation
    }

    public abstract void update(float dt);

    public abstract void physicsUpdate(float dt);

    public abstract void render();

    public void stop() {
        if (state != KronosState.RUNNING) throw new IllegalStateException("Cannot stop application in state " + state);
        state = state.next();
        
        // implementation
    }

    public void close() {
        if (state != KronosState.STOPPING) throw new IllegalStateException("Cannot close application in state " + state);
        state = state.next();
        
        // implementation
    }
    
}
