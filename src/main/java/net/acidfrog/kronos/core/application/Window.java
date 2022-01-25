package net.acidfrog.kronos.core.application;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import net.acidfrog.kronos.core.Config;
import net.acidfrog.kronos.core.lang.assertions.Asserts;
import net.acidfrog.kronos.core.lang.logger.Logger;

public class Window {
	
	private static Window instance = null;

	private int width, height;
	private int fps, ups;
	private String title;
	private long pointer;
	private KronosApplication application;

	private Window(int width, int height, String title, KronosApplication application) {
		this.width = width;
		this.height = height;
		this.title = title;
		this.application = application;
	}

	public static Window create(int width, int height, String title, KronosApplication application) {
		if (instance == null) instance = new Window(width, height, title, application);
		return instance;
	}
	
	public void initialize() {
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
			Logger.instance.logInfo("GLFW Window " + pointer + " closed");
			EntryPoint.close();
		});

		GLFW.glfwShowWindow(pointer);
		GL.createCapabilities();
		
		Logger.instance.logInfo("LWJGL Version " + Version.getVersion());
	}

	public void run() {
		boolean render = false;
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1_000_000_000 / amountOfTicks;
		float deltaTime = 0;
		long timer = System.currentTimeMillis();
		int ticks = 0;
		int frames = 0;

		while (!GLFW.glfwWindowShouldClose(pointer)) {
			render = true;
			long now = System.nanoTime();
			deltaTime += (now - lastTime) / ns;
			lastTime = now;

			GLFW.glfwPollEvents();

			while (deltaTime >= 1) {
				render = true;
				update(deltaTime);
				physicsUpdate(deltaTime);
				ticks++;
				deltaTime--;
			}
			
			if (render) {
				render();
				frames++;
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				fps = frames;
				ups = ticks;
				frames = 0;
				ticks = 0;
				System.out.println("FPS: " + fps + " UPS: " + ups);
			}
		}

		GLFW.glfwDestroyWindow(pointer);
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
	}

	public void update(float dt) {
		application.update(dt);
	}

	public void physicsUpdate(float pdt) {
		application.physicsUpdate(pdt);
	}

	public void render() {
		GL11.glClearColor(0.0f, 0f, 0f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		application.render();
		GLFW.glfwSwapBuffers(pointer);
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

	public long getPointer() {
		return pointer;
	}
	
}
