package net.acidfrog.kronos.scene.ecs.signal;

/**
 * A functional interface that can be registered to {@link Signal}s. When a
 * {@link Signal} is triggered, the {@link SignalListener} will be notified.
 * 
 * @since 0.0.2
 * @version 0.0.2
 * @author Ethan Temprovich
 */
@FunctionalInterface
public interface SignalListener<T> {

    /**
     * Called by a registered {@link Signal} when it is triggered.
     * 
     * @param data
     */
    public abstract void receive(T data);
    
}
