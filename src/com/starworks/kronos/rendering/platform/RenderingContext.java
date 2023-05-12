package com.starworks.kronos.rendering.platform;

public sealed interface RenderingContext permits OpenGLContext {

	public void initialize();
	
	public void swapBuffers();
}
