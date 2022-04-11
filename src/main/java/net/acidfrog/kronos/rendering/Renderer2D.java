package net.acidfrog.kronos.rendering;

import java.util.Deque;

import net.acidfrog.kronos.core.datastructure.Deck;
import net.acidfrog.kronos.rendering.component.SpriteRendererComponent;
import net.acidfrog.kronos.scene.component.TransformComponent;
import net.acidfrog.kronos.scene.ecs.Entity;
import net.acidfrog.kronos.scene.ecs.Family;

public class Renderer2D {
    
    static final Family REQUIRED = Family.define(SpriteRendererComponent.class, TransformComponent.class);
    static final int MAX_BATCH_SIZE = 10000;

    private Deque<Batch> batches;

    public Renderer2D() {
        this.batches = new Deck<Batch>();
    }
    
    public void add(Entity entity) {
        if (REQUIRED.excludes(entity)) return;

        boolean added = false;

        for (Batch batch : batches) {
            if (batch.isFull()) continue;
            
            Texture texture = entity.get(SpriteRendererComponent.class).getTexture();

            if (texture == null || batch.hasTexture(texture) || batch.hasTextureSlotsAvailable()) {
                batch.add(entity);
                added = true;
                break;
            }
        }

        if (!added) {
            Batch batch = new Batch();
            batch.add(entity);
            batches.push(batch);
            batch.begin();
        }
    }

    public void render() {
        for (Batch batch : batches) batch.render();
    }

}
