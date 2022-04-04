package net.acidfrog.kronos.scene.ecs.system;

import java.util.Comparator;

import net.acidfrog.kronos.scene.ecs.Entity;
import net.acidfrog.kronos.scene.ecs.Registry;

/**
 * Represents a system that processes {@link Entity entities} in a {@link Registry}.
 * 
 * @since 0.0.2
 * @version 0.0.2
 * @author Ethan Temprovich
 */
public interface EntitySystem {

    /**
     * Steps the system.
     * 
     * <p>
     * This method should be called once per frame.
     * 
     * @param dt
     */
    public abstract void update(float dt);

    /**
     * Update not bound to delta time. Useful for rendering systems.
     */
    public default void update() {
    }

    /**
     * Binds the system to the specified {@link Registry}.
     * 
     * @param registry
     */
    public abstract void bind(Registry registry);

    /**
     * Unbinds the system from its {@link Registry}.
     */
    public abstract void unbind();

    /**
     * Called by the {@link Registry} when the system is bound.
     * 
     * @param registry
     */
    public abstract void onBind(Registry registry);

    /**
     * Called by the {@link Registry} when the system is unbound.
     */
    public abstract void onUnbind(Registry registry);

    /**
     * Called when the system is enabled/disabled.
     */
    public abstract void onStateChange();

    /**
     * @return the priority of the system.
     */
    public abstract int getPriority();

    /**
     * @return the {@link Registry} this system is bound to.
     */
    public abstract Registry registry();
    
    /**
     * @return whether the system is currently processing.
     */
    public abstract boolean isProcessing();

    /**
     * Sets the processing state of the system.
     * 
     * @param processing
     */
    public abstract void setProcessing(boolean processing);

    /**
     * @return true if the system is enabled, false otherwise.
     */
    public abstract boolean isEnabled();

    /**
     * Sets the enabled state of the system.
     */
    public abstract void enable();

    /**
     * Sets the disable state of the system.
     */
    public abstract void disable();

    /**
     * This comparator is used to sort systems based on their priority.
     * 
     * @author Ethan Temprovich
     */
    public static final class SystemComparator implements Comparator<EntitySystem> {

        /**
         * @inheritDoc
         */
        @Override
        public int compare(EntitySystem a, EntitySystem b) {
            return a.getPriority() > b.getPriority() ? 1 : (a.getPriority() == b.getPriority()) ? 0 : -1;
        }
        
    }

}
