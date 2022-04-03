package net.acidfrog.kronos.rendering;

import static org.lwjgl.opengl.GL30.*;

import net.acidfrog.kronos.core.lang.logger.Logger;

public class Framebuffer {

    private int fboID;
    private Texture texture;

    public Framebuffer(int width, int height) {
        this.fboID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
        
        this.texture = new Texture(width, height);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getID(), 0);

        int rboID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboID);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) Logger.instance.logError("Framebuffer is not complete!");
        
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void delete() {
        glDeleteFramebuffers(fboID);
    }

    public Texture getTexture() {
        return texture;
    }

    public int getID() {
        return fboID;
    }
    
}
