package net.acidfrog.kronos.scene.ecs;

public interface EntityListener {
    
    public void onEntityAdd(Entity entity);
    
    public void onEntityRemove(Entity entity);
    
}
