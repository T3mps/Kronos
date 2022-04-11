package net.acidfrog.kronos.scene.ecs.component;

import net.acidfrog.kronos.scene.ecs.Entity;

/**
 * Default implementation of a {@link Component}.
 * 
 * @since 0.0.2
 * @version 0.0.2
 * @author Ethan Temprovich
 */
public abstract class AbstractComponent implements Component {

    /** The entity this component is attached to */
    protected Entity parent;

    /** Indicates if this component is enabled */
    protected boolean enabled;

    /**
     * Default constructor.
     */
    public AbstractComponent() {
        this.enabled = false;
    }
    
    /**
     * @inheritDoc
     */
    @Override
    public final Entity getParent() {
        return parent;
    }
    
    /**
     * @inheritDoc
     */
    @Override
    public final void setParent(Entity parent) {
        this.parent = parent;
    }

    /**
     * @inheritDoc
     */
    @Override
    public final boolean isEnabled() {
        return enabled;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void enable() {
        if (enabled) return;
        enabled = true;
        onEnable();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onEnable() {}
    
    /**
     * @inheritDoc
     */
    @Override
    public void disable() {
        if (!enabled) return;
        enabled = false;
        onDisable();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onDisable() {}

}
