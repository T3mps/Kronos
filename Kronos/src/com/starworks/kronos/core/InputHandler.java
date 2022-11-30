package com.starworks.kronos.core;

import static org.lwjgl.glfw.GLFW.*;
// GLFW Window input handler
public final class InputHandler {
    
    private boolean keys[] = new boolean[1024];
    private boolean mouseButtons[] = new boolean[32];
    private double mouseX, mouseY, lastMouseX, lastMouseY;
    private double scrollX, scrollY;

    // glfw
    public InputHandler(long window) {
        for (int i = 0; i < keys.length; i++) {
            keys[i] = false;
        }
        for (int i = 0; i < mouseButtons.length; i++) {
            mouseButtons[i] = false;
        }

        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            keys[key] = action != GLFW_RELEASE;
        });
        glfwSetMouseButtonCallback(window, (w, button, action, mods) -> {
            mouseButtons[button] = action != GLFW_RELEASE;
        });
        glfwSetCursorPosCallback(window, (w, xpos, ypos) -> {
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            mouseX = xpos;
            mouseY = ypos;
        });
        glfwSetScrollCallback(window, (w, xoffset, yoffset) -> {
            scrollX = xoffset;
            scrollY = yoffset;
        });
    }

    public boolean isKeyPressed(int key) {
        return keys[key];
    }

    public boolean isMouseButtonPressed(int button) {
        return mouseButtons[button];
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

    public double getDeltaX() {
        return mouseX - lastMouseX;
    }

    public double getDeltaY() {
        return mouseY - lastMouseY;
    }

    public double getScrollX() {
        return scrollX;
    }

    public double getScrollY() {
        return scrollY;
    }
}
