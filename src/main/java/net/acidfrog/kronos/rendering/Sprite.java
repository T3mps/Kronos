package net.acidfrog.kronos.rendering;

import java.util.Arrays;

import net.acidfrog.kronos.math.Vector2f;

public class Sprite {

    private static final Vector2f[] DEFAULT_UVs = new Vector2f[] { new Vector2f(1, 1),
                                                                   new Vector2f(1, 0),
                                                                   new Vector2f(0, 0),
                                                                   new Vector2f(0, 1), };

    private Texture texture;
    private Vector2f[] uvs;

    public Sprite(Texture texture) {
        this(texture, DEFAULT_UVs);
    }

    public Sprite(Texture texture, Vector2f[] uvs) {
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Sprite))
            return false;
        Sprite other = (Sprite) obj;
        if (texture == null) {
            if (other.texture != null)
                return false;
        } else if (!texture.equals(other.texture))
            return false;
        if (!Arrays.equals(uvs, other.uvs))
            return false;
        return true;
    }

}
