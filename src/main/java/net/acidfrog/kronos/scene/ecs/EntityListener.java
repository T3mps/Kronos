package net.acidfrog.kronos.scene.ecs;

public interface EntityListener {
    
    public void onEntityAdd(Entity e);
    
    public void onEntityRemove(Entity e);
    
}
