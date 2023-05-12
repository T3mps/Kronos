package com.starworks.kronos.rendering.platform;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

public final class OpenGLContext implements RenderingContext {

	private final long m_windowPointer;
	
	public OpenGLContext(long windowPointer) {
		this.m_windowPointer = windowPointer;
	}
	
	@Override
	public void initialize() {
		GLFW.glfwMakeContextCurrent(m_windowPointer);
		GL.createCapabilities();
	}

	@Override
	public void swapBuffers() {
		GLFW.glfwSwapBuffers(m_windowPointer);
	}
}
