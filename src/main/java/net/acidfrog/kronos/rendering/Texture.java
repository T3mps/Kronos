package net.acidfrog.kronos.rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import net.acidfrog.kronos.core.lang.logger.Logger;

public class Texture {

    private static final String GENERATED_TEXTURE_PATH = "generated_texture";

    private int width;
    private int height;
    private String path;
    private int id;

    public Texture() {
        this(-1, -1);
        this.id = -1;
    }

    public Texture(int width, int height) {
        this.width = width;
        this.height = height;
        this.path = GENERATED_TEXTURE_PATH;
        this.id = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, id);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
    }

    public Texture initialize(String path) {
        this.path = path;
        this.id = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, id);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer data = stbi_load(path, width, height, channels, 4);

        if (data == null) {
            Logger.instance.logError("Failed to load texture: " + path);
            return null;
        }

        this.width = width.get(0);
        this.height = height.get(0);

        if (channels.get(0) == 3) glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, data);
        else if (channels.get(0) == 4) glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        else {
            Logger.instance.logError("Failed to load texture: " + path);
            return null;
        }

        stbi_image_free(data);

        return this;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void delete() {
        glDeleteTextures(id);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getPath() {
        return path;
    }

    public int getID() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Texture)) return false;

        Texture t = (Texture) o;
        return t.width == this.width     &&
               t.height == this.height   &&
                t.path.equals(this.path) &&
                t.id == this.id;
    }
    
}
