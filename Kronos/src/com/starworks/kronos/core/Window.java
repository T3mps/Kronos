package com.starworks.kronos.core;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class Window {
    
    private String title;
    private int width, height;
    private long window;
    private InputHandler inputHandler;

    public Window(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
    }

    public void create() {
        if (!glfwInit()) {
            System.err.println("GLFW failed to initialize!");
            System.exit(1);
        }

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL) {
            System.err.println("GLFW failed to create window!");
            System.exit(1);
        }

        this.inputHandler = new InputHandler(window);

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
        });

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
    }

    public void update() {
        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    public void destroy() {
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getWindow() {
        return window;
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }
}
