package net.acidfrog.kronos.scene.ecs;

public abstract class EngineSystem {
    
    private Engine engine;
    private boolean enabled = true;

    public void update(float dt) {}

    public void onEngineBind(Engine engine) {}

    public void onEngineUnbind(Engine engine) {}

    public void onStateChange() {}

    public final Engine getContext() {
        return engine;
    }

    final void bind(Engine engine) {
        this.engine = engine;
    }

    final void unbind() {
        engine = null;
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public final void enable() {
        if (enabled) return;
        enabled = true;
        onStateChange();
    }

    public final void disable() {
        if (!enabled) return;
        enabled = false;
        onStateChange();
    }

}
