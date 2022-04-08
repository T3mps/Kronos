package net.acidfrog.kronos.rendering.component;

import net.acidfrog.kronos.math.Vector2f;
import net.acidfrog.kronos.math.Vector4f;
import net.acidfrog.kronos.rendering.Sprite;
import net.acidfrog.kronos.rendering.Texture;
import net.acidfrog.kronos.scene.ecs.component.AbstractComponent;

public class SpriteRendererComponent extends AbstractComponent {

    private Sprite sprite;
    private Vector4f color;

    public SpriteRendererComponent(Vector4f color) {
        this.sprite = new Sprite(null);
        this.color = color;
    }

    public SpriteRendererComponent(Sprite sprite) {
        this.sprite = sprite;
        this.color = new Vector4f(1f, 1f, 1f, 1f);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public boolean hasTexture() {
        return sprite.hasTexture();
    }

    public Vector2f[] getUVs() {
        return sprite.getUVs();
    }

    public Vector4f getColor() {
        return color;
    }

}
