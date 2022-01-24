package net.acidfrog.kronos.core;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import net.acidfrog.kronos.core.lang.assertions.Asserts;
import net.acidfrog.kronos.core.lang.input.InputHandler;
import net.acidfrog.kronos.core.lang.logger.Logger;

public class Window {
	
	private static Window instance = null;

	private int width, height;
	private String title;
	private long pointer;

	private Window(int width, int height, String title) {
		this.width = width;
		this.height = height;
		this.title = title;
	}

	public static Window create(int width, int height, String title) {
		if (instance == null) instance = new Window(width, height, title);
		return instance;
	}
	
	private void initialize() {
		GLFWErrorCallback.createPrint(Config.ERROR_STREAM).set();

		Asserts.assertTrue(GLFW.glfwInit(), "Failed to initialize GLFW!");

		if (!GLFW.glfwInit()) {
			Logger.instance.logFatal("Failed to initialize GLFW");
			return;
		}
		
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);

		this.pointer = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);

		Asserts.assertFalse(pointer == MemoryUtil.NULL, "Failed to create GLFW window!");

		GLFW.glfwMakeContextCurrent(pointer);
		GLFW.glfwSwapInterval(1);
		GLFW.glfwSetWindowSize(pointer, width, height);
		GLFW.glfwSetWindowTitle(pointer, title);
		GLFW.glfwFocusWindow(pointer);
		
		GLFW.glfwSetWindowCloseCallback(pointer, (window) -> {
			Logger.instance.logInfo("GLFW window " + pointer + " closed");
		});

		GLFW.glfwShowWindow(pointer);
		GL.createCapabilities();
	}
	
	public void run() {
		Logger.instance.logInfo(Version.getVersion());
		
		initialize();
		
		while (!GLFW.glfwWindowShouldClose(pointer)) {
			GLFW.glfwPollEvents();

			System.out.println(InputHandler.instance.isMouseDragging());

			GL11.glClearColor(1.0f, 0f, 0f, 1.0f);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

			GLFW.glfwSwapBuffers(pointer);
		}

		GLFW.glfwDestroyWindow(pointer);
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getTitle() {
		return title;
	}
	
}
