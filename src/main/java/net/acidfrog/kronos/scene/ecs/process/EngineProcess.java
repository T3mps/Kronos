package net.acidfrog.kronos.scene.ecs.process;

import net.acidfrog.kronos.scene.ecs.Engine;

public abstract sealed class EngineProcess permits ComponentProcessor {

    private Engine engine;
    private boolean enabled = true;

    public EngineProcess() {}

    public void update(float dt) {}

    public void onEngineBind(Engine engine) {}

    public void onEngineUnbind(Engine engine) {}

    public void onStateChange() {}

    public Engine getEngine() {
        return engine;
    }

    public EngineProcess bind(Engine engine) {
        this.engine = engine;
        return this;
    }

    public EngineProcess unbind() {
        engine = null;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public EngineProcess enable() {
        enabled = true;
        return this;
    }

    public EngineProcess disable() {
        enabled = false;
        onStateChange();
        return this;
    }
    
}
