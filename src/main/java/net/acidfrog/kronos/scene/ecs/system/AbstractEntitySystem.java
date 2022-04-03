package net.acidfrog.kronos.scene.ecs.system;

import net.acidfrog.kronos.scene.ecs.Registry;

/**
 * Default implementation of {@link EntitySystem}.
 * 
 * @since 0.0.2
 * @version 0.0.2
 * @author Ethan Temprovich
 */
public class AbstractEntitySystem implements EntitySystem {
    
    /** Importance of system */
    protected int priority;

    /** Reference to binded registry */
    protected Registry registry;

    /** Indicates if the system is updating */
    protected boolean processing;

    /** Indicates if the system is enabled */
    protected boolean enabled;

    /**
     * Default constructor.
     */
    public AbstractEntitySystem() {
        this(0);
    }

    /**
     * Creates a new system with the specified priority.
     * 
     * @param priority
     */
    public AbstractEntitySystem(int priority) {
        this.priority = priority;
        this.processing = true;
        this.enabled = true;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void update(float dt) {}
    
    /**
     * @inheritDoc
     */
    @Override
    public void bind(Registry registry) {
        this.registry = registry;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void unbind() {
        this.registry = null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onBind(Registry registry) {}

    /**
     * @inheritDoc
     */
    @Override
    public void onUnbind(Registry registry) {}

    /**
     * @inheritDoc
     */
    @Override
    public void onStateChange() {}

    /**
     * @inheritDoc
     */
    @Override
    public int getPriority() {
        return priority;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Registry registry() {
        return registry;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isProcessing() {
        return processing;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void setProcessing(boolean processing) {
        this.processing = processing;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void enable() {
        if (enabled) return;
        enabled = true;
        onStateChange();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void disable() {
        if (!enabled) return;
        enabled = false;
        onStateChange();
    }

}
