package net.acidfrog.kronos.core.util;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {
    
    private static InputHandler instance;

    private double mouseX;
    private double mouseY;
    private double mouseLastX;
    private double mouseLastY;
    private double mouseScrollX;
    private double mouseScrollY;
    private boolean[] mouseButtons = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];

    private boolean[] keys = new boolean[GLFW_KEY_LAST + 1];

    private InputHandler(long windowPointer) {
        glfwSetCursorPosCallback(windowPointer, (window, xpos, ypos) -> {
            mouseLastX = mouseX;
            mouseLastY = mouseY;
            mouseX = xpos;
            mouseY = ypos;
        });
        
        glfwSetScrollCallback(windowPointer, (window, xoffset, yoffset) -> {
            mouseScrollX = xoffset;
            mouseScrollY = yoffset;
        });

        glfwSetMouseButtonCallback(windowPointer, (window, button, action, mods) -> {
            mouseButtons[button] = action == GLFW_PRESS;
        });

        glfwSetKeyCallback(windowPointer, (window, key, scancode, action, mods) -> {
            keys[key] = action == GLFW_PRESS;
        });
    }

    public static void initialize(long ptr) {
        instance = new InputHandler(ptr);
    }

    public static InputHandler getInstance() {
        return instance;
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }
    
    public double getMouseDX() {
        return mouseX - mouseLastX;
    }

    public double getMouseDY() {
        return mouseY - mouseLastY;
    }

    public double getMouseScrollX() {
        return mouseScrollX;
    }

    public double getMouseScrollY() {
        return mouseScrollY;
    }

    public double getMouseScroll() {
        return mouseScrollY;
    }
    
    public boolean isMouseButtonDown(int button) {
        return mouseButtons[button];
    }

    public boolean isKeyDown(int key) {
        return keys[key];
    }

}
