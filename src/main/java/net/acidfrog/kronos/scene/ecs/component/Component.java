package net.acidfrog.kronos.scene.ecs.component;

import net.acidfrog.kronos.scene.ecs.Entity;

public abstract class Component implements Comparable<Component> {

    private Entity parent;
    private boolean enabled;

    public Component() {}
    
    public final Entity getParent() {
        return parent;
    }
    
    public final void setParent(Entity parent) {
        this.parent = parent;
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public void enable() {
        if (enabled) return;
        enabled = true;
        onEnable();
    }

    protected void onEnable() {}
    
    public void disable() {
        if (!enabled) return;
        enabled = false;
        onDisable();
    }

    protected void onDisable() {}

}
