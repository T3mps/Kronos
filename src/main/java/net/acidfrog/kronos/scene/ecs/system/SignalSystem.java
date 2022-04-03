package net.acidfrog.kronos.scene.ecs.system;

import net.acidfrog.kronos.scene.ecs.signal.Signal;
import net.acidfrog.kronos.scene.ecs.signal.SignalListener;

/**
 * Abstract implementation implementation of {@link EntitySystem}, which operates on
 * a {@link Signal}. This is also an implementation of a {@link SignalListener}, which
 * is how the system will be notified of signals.
 * 
 * @since 0.0.2
 * @version 0.0.2
 * @author Ethan Temprovich
 */
public abstract class SignalSystem<T> extends AbstractEntitySystem implements SignalListener<T> {

    /**
     * Creates a new system and registers it to the specified {@link Signal}.
     * 
     * @param signal
     */
    public SignalSystem(Signal<T> signal) {
        this(signal, 0);
    }

    /**
     * Creates a new system with the given priority, and registers it to the specified
     * {@link Signal}.
     * 
     * @param signal
     * @param priority
     */
    public SignalSystem(Signal<T> signal, int priority) {
        super(priority);
        signal.register(this);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void receive(T data) {
        push();

        process(data);

        pop();
    }

    /**
     * Called directly before the system processes any data.
     */
    protected void push() {}

    /**
     * Determines the method at which the system processes the data.
     */
    protected abstract void process(T data);
    
    /**
     * Called directly after the system processes any data.
     */
    protected void pop() {}

}
