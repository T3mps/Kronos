package net.acidfrog.kronos.scene.ecs;

public interface EntityListener {

    public abstract void onEntityAdd(Entity entity);

    public abstract void onEntityRemove(Entity entity);
    
}
