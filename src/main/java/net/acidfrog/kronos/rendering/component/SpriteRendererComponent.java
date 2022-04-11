package net.acidfrog.kronos.rendering.component;

import net.acidfrog.kronos.math.Vector2f;
import net.acidfrog.kronos.math.Vector4f;
import net.acidfrog.kronos.rendering.Sprite;
import net.acidfrog.kronos.rendering.Texture;
import net.acidfrog.kronos.scene.ecs.component.AbstractComponent;

public class SpriteRendererComponent extends AbstractComponent {

    private Sprite sprite;
    private Vector4f color;

    private boolean dirty;

    public SpriteRendererComponent(Vector4f color) {
        this.sprite = new Sprite(null);
        this.color = color;
        this.dirty = true;
    }

    public SpriteRendererComponent(Sprite sprite) {
        this.sprite = sprite;
        this.color = new Vector4f(1f, 1f, 1f, 1f);
        this.dirty = true;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        if (!this.sprite.equals(sprite)) {
            dirty = true;
            this.sprite = sprite;
        }
    }

    public Vector4f getColor() {
        return color;
    }

    public void setColor(Vector4f color) {
        if (!this.color.equals(color)) {
            this.color.set(color);
            dirty = true;
        }
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

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

}
