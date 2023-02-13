package com.starworks.kronos.core;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.util.Objects;

import org.lwjgl.glfw.GLFWErrorCallback;

import com.starworks.kronos.exception.Exceptions;
import com.starworks.kronos.exception.KronosRuntimeException;
import com.starworks.kronos.logging.Level;
import com.starworks.kronos.logging.Logger;

public class GLFWContext {
	private static final Logger LOGGER = Logger.getLogger("Main", GLFWContext.class, Level.ERROR);
	
	

	private GLFWContext() {}
	
	public static final void init() {
		GLFWErrorCallback callback = GLFWErrorCallback.create((int error, long description) -> {
		    LOGGER.error("GLFW error [" + error + "]: " + GLFWErrorCallback.getDescription(description));
		});
		glfwSetErrorCallback(callback);
		if (!glfwInit()) {
			throw new KronosRuntimeException(Exceptions.getMessage("core.window.initializationFailed"));
		}
	}
	
	public static void terminate() {
		glfwTerminate();
		Objects.requireNonNull(glfwSetErrorCallback(null)).free();
	}
}
