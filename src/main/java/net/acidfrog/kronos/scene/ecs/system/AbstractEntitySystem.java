package net.acidfrog.kronos.scene.ecs.system;

import net.acidfrog.kronos.scene.ecs.Registry;

public class AbstractEntitySystem implements EntitySystem {
    
    private int priority;

    private Registry registry;

    private boolean processing;
    private boolean enabled;

    public AbstractEntitySystem() {
        this(0);
    }

    public AbstractEntitySystem(int priority) {
        this.priority = priority;
        this.processing = true;
        this.enabled = true;
    }

    @Override
    public void update(float dt) {}
    
    @Override
    public void bind(Registry registry) {
        this.registry = registry;
    }

    @Override
    public void unbind() {
        this.registry = null;
    }

    @Override
    public void onBind(Registry registry) {}

    @Override
    public void onUnbind(Registry registry) {}

    @Override
    public void onStateChange() {}

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public Registry registry() {
        return registry;
    }

    @Override
    public boolean isProcessing() {
        return processing;
    }

    @Override
    public void setProcessing(boolean processing) {
        this.processing = processing;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void enable() {
        if (enabled) return;
        enabled = true;
        onStateChange();
    }

    @Override
    public void disable() {
        if (!enabled) return;
        enabled = false;
        onStateChange();
    }

}
