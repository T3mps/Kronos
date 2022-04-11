package net.acidfrog.kronos.rendering;

import java.util.HashMap;
import java.util.Map;

import net.acidfrog.kronos.rendering.component.SpriteRendererComponent;
import net.acidfrog.kronos.scene.component.TransformComponent;
import net.acidfrog.kronos.scene.ecs.Entity;
import net.acidfrog.kronos.scene.ecs.system.IterativeSystem;

public class RenderingSystem extends IterativeSystem {

    private Map<SpriteRendererComponent, TransformComponent> componentMap;

    private SpriteRendererComponent sprite;
    private TransformComponent transform;

    public RenderingSystem() {
        super(Renderer2D.REQUIRED);
        this.componentMap = new HashMap<SpriteRendererComponent, TransformComponent>(entities.size());
    }

    @Override
    protected void process(Entity entity, float dt) {
        sprite = entity.get(SpriteRendererComponent.class);
        transform = entity.get(TransformComponent.class);
        
        if (!transform.equals(componentMap.get(sprite))) {
            componentMap.put(sprite, transform);
            sprite.setDirty(true);
        }
    }
    
}
