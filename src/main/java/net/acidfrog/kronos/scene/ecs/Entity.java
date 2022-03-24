package net.acidfrog.kronos.scene.ecs;
import net.acidfrog.kronos.scene.ecs.signal.Signal;

public final class Entity {

    public int flags;
    public final Signal<Entity> onComponentAdd;
    public final Signal<Entity> onComponentRemove;
    
}
