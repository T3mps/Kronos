package net.acidfrog.kronos.core.architecture;

import net.acidfrog.kronos.core.Config;
import net.acidfrog.kronos.core.Version;
import net.acidfrog.kronos.core.util.InputHandler;
import net.acidfrog.kronos.math.Vector4f;

/**
 * 
 * @apiNote When overriding non-abstract methods inherited from this class, it 
 *         is crucial to call super.method() to ensure that the default behavior
 *         is executed. Otherwise, you will have to write your own code to handle
 *         backend bindings, such as GLFW.
 */
public abstract class AbstractApplication implements Runnable {

    public static final int DEFAULT_WINDOW_WIDTH = 1280;
    public static final int DEFAULT_WINDOW_HEIGHT = 720;
    public static final String DEFAULT_WINDOW_TITLE = "kronos " + Version.get() + " [" + Config.OPERATING_SYSTEM + " " + Config.OPERATING_SYSTEM_ARCHITECTURE + "]";

    protected static int fps = 60;
	protected static int ups = 60;

    protected KronosState state;
    protected Window window;
    protected volatile boolean running = false;

    public AbstractApplication(String windowTitle, int... args) {
        Config.getInstance().load();
        this.state = KronosState.ENTRY;
        this.window = (args.length >= 2) ? new Window(args[0], args[1], windowTitle) : new Window();
        initialize();
    }
    
    protected void initialize() {
        if (state != KronosState.ENTRY) throw new IllegalStateException("Cannot initialize application in state " + state);
        state = state.next();
        
        window.initialize()
              .vsync(false)
              .setClearColor(new Vector4f(0f, 0f, 0f, 0f));
              
        InputHandler.initialize(window.pointer());
    }

    public void start() {
        if (state != KronosState.INITIALIZING) throw new IllegalStateException("Cannot start application in state " + state);
        state = state.next();
        
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
