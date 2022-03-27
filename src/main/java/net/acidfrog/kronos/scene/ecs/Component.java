package net.acidfrog.kronos.scene.ecs;

public abstract class Component {

    private Entity parent;
    private boolean enabled;

    public Component() {}
    
    public final Entity getParent() {
        return parent;
    }
    
    final void setParent(Entity parent) {
        this.parent = parent;
    }

    protected final boolean isEnabled() {
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
