package net.acidfrog.kronos.scene.ecs;

/**
 * Interface that is notified of entity-related events.
 * 
 * @since 0.0.2
 * @version 0.0.2
 * @author Ethan Temprovich
 */
public interface EntityListener {
    
    /**
     * Triggered when an entity is added to the {@link Registry}.
     * 
     * @param entity
     */
    public void onEntityAdd(Entity entity);
    
    /**
     * Trigger when an entity is removed from the {@link Registry}.
     * 
     * @param entity
     */
    public void onEntityRemove(Entity entity);
    
}
