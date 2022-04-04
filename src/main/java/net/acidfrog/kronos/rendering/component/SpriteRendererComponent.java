package net.acidfrog.kronos.rendering.component;

import java.util.Vector;

import net.acidfrog.kronos.math.Vector4f;
import net.acidfrog.kronos.scene.ecs.component.AbstractComponent;

public class SpriteRendererComponent extends AbstractComponent {

    private Vector4f color;

    public SpriteRendererComponent(Vector4f color) {
        this.color = color;
    }

    @Override
    public void onEnable() {
    }

    public Vector4f getColor() {
        return color;
    }
    
}
