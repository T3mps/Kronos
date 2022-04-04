package net.acidfrog.kronos.rendering;

import java.util.ArrayList;
import java.util.List;

import net.acidfrog.kronos.rendering.component.SpriteRendererComponent;
import net.acidfrog.kronos.scene.component.TransformComponent;
import net.acidfrog.kronos.scene.ecs.Entity;
import net.acidfrog.kronos.scene.ecs.Family;

public class Renderer2D {
    
    static final Family REQUIRED = Family.define(SpriteRendererComponent.class, TransformComponent.class);
    static final int MAX_BATCH_SIZE = 1000;

    private List<Batch> batches;

    public Renderer2D() {
        this.batches = new ArrayList<Batch>();
    }
    
    public void add(Entity entity) {
        if (REQUIRED.excludes(entity)) return;

        boolean added = false;

        for (Batch batch : batches) {
            if (batch.isFull()) continue;
            else {
                batch.add(entity);
                added = true;
                break;
            }
        }

        if (!added) {
            Batch batch = new Batch();
            batch.add(entity);
            batch.begin();
            batches.add(batch);
        }
    }

    public void render() {
        for (Batch batch : batches) batch.render();
    }

}
