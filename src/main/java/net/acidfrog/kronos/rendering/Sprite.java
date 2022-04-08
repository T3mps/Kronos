package net.acidfrog.kronos.rendering;

import net.acidfrog.kronos.math.Vector2f;
import net.acidfrog.kronos.math.Vector4f;

public class Sprite {

    private static final Vector2f[] DEFAULT_UVs = new Vector2f[] { new Vector2f(1, 1),
                                                                   new Vector2f(1, 0),
                                                                   new Vector2f(0, 0),
                                                                   new Vector2f(0, 1), };

    private Texture texture;
    private Vector2f[] uvs;

    public Sprite(Texture texture) {
        this(texture, new Vector4f(1f, 1f, 1f, 1f));
    }

    public Sprite(Texture texture, Vector4f color) {
        this(texture, DEFAULT_UVs, color);
    }

    public Sprite(Texture texture, Vector2f[] uvs, Vector4f color) {
        this.texture = texture;
        this.uvs = uvs;
    }

    public Texture getTexture() {
        return texture;
    }

    public boolean hasTexture() {
        return texture != null;
    }

    public Vector2f[] getUVs() {
        return uvs;
    }
    
}
