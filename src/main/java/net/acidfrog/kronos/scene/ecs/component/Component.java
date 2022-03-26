package net.acidfrog.kronos.scene.ecs.component;

import net.acidfrog.kronos.scene.ecs.Entity;

public abstract class Component {

    private Entity parent;

    private boolean enabled;

    public Component() {
        this.parent = null;
        this.enabled = false;
    }

    public void onEnable() {}

    public void onDisable() {}

    public Entity getParent() {
        return parent;
    }

    public Component setParent(Entity parent) {
        this.parent = parent;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void enable() {
        if (enabled) return;
        enabled = true;
        onEnable();
    }

    public void disable() {
        if (!enabled) return;
        enabled = false;
        onDisable();
    }

}
