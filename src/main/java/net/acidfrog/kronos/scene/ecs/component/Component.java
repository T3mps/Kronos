package net.acidfrog.kronos.scene.ecs.component;

import net.acidfrog.kronos.scene.ecs.Entity;

/**
 * Components can be added to entities, and are be used to give them
 * functionality. The data that is stored in a component is used by the
 * relavent systems to process the entity.
 * 
 * @since 0.0.2
 * @version 0.0.2
 * @author Ethan Temprovich
 */
public interface Component {

    /**
     * @return The parent entity that this component is attached to.
     */
    public abstract Entity getParent();

    /**
     * Sets the parent entity of this component.
     */
    public abstract void setParent(Entity parent);

    /**
     * @return True if this component is enabled, false otherwise.
     */
    public abstract boolean isEnabled();

    /**
     * Enables this component.
     */
    public abstract void enable();

    /**
     * Called when this component is enabled.
     */
    public abstract void onEnable();

    /**
     * Disables this component.
     */
    public abstract void disable();

    /**
     * Called when this component is disabled.
     */
    public abstract void onDisable();
    
}
