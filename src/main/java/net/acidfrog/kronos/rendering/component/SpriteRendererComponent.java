package net.acidfrog.kronos.rendering.component;

import net.acidfrog.kronos.math.Vector2f;
import net.acidfrog.kronos.math.Vector4f;
import net.acidfrog.kronos.rendering.Texture;
import net.acidfrog.kronos.scene.ecs.component.AbstractComponent;

public class SpriteRendererComponent extends AbstractComponent {

    private Vector2f[] uvs;
    private Texture texture;
    private Vector4f color;

    public SpriteRendererComponent(Vector4f color) {
        this.texture = null;
        this.color = color;
    }

    public SpriteRendererComponent(Texture texture) {
        this.texture = texture;
        this.color = new Vector4f(1, 1, 1, 1);
    }

    @Override
    public void onEnable() {
    }

    public Vector2f[] getUVs() {
        return new Vector2f[]
            {
               new Vector2f(1, 1),
               new Vector2f(1, 0),
               new Vector2f(0, 0),
               new Vector2f(0, 1)
            };
    }

    public Texture getTexture() {
        return texture;
    }

    public boolean hasTexture() {
        return texture != null;
    }

    public Vector4f getColor() {
        return color;
    }

}
