package net.acidfrog.kronos.rendering;

import net.acidfrog.kronos.core.assets.AssetManager;
import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;
import net.acidfrog.kronos.core.lang.error.KronosRenderError;
import net.acidfrog.kronos.math.Vector2f;

public final class Spritesheet {

    private final Texture texture;
    private final Sprite[] sprites;

    private Spritesheet(Texture texture, int spriteWidth, int spriteHeight, int spriteCount, int spacing) {
        this.texture = texture;
        this.sprites = new Sprite[spriteCount];

        int cx = 0;
        int cy = texture.getHeight() - spriteHeight;
        
        for (int i = 0; i < spriteCount; i++) {
            float ty = (cy + spriteHeight) / (float) texture.getHeight();
            float rx = (cx + spriteWidth) / (float) texture.getWidth();
            float lx = cx / (float) texture.getWidth();
            float by = cy / (float) texture.getHeight();

            Vector2f[] uvs = new Vector2f[] { new Vector2f(rx, ty),
                                              new Vector2f(rx, by),
                                              new Vector2f(lx, by),
                                              new Vector2f(lx, ty), };
            
            Sprite sprite = new Sprite(texture, uvs);
            sprites[i] = sprite;

            cx += spriteWidth + spacing;
            if (cx >= texture.getWidth()) {
                cx = 0;
                cy -= spriteHeight + spacing;

                if (cy < 0) break;
            }
        }

        if (sprites[spriteCount - 1] == null) throw new KronosRenderError(KronosErrorLibrary.SPRITE_COUNT_MISMATCH);
    }

    public static Spritesheet create(Texture texture, int spriteWidth, int spriteHeight, int spriteCount, int spacing) {
        if (texture == null) throw new KronosRenderError(KronosErrorLibrary.TEXTURE_NOT_FOUND);

        Spritesheet sheet = new Spritesheet(texture, spriteWidth, spriteHeight, spriteCount, spacing);
        AssetManager.addSpritesheet(texture.getPath(), sheet);
        return sheet;
    }

    public Sprite get(int index) {
        return sprites[index];
    }

    public Texture getTexture() {
        return texture;
    }
    
}
