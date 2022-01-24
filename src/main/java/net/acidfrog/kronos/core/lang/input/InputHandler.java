package net.acidfrog.kronos.core.lang.input;

import org.lwjgl.glfw.GLFW;

public class InputHandler {

    public static final InputHandler instance = new InputHandler();

    private double currentMouseX, currentMouseY;
    private double lastMouseX, lastMouseY;
    private double scrollX, scrollY;
    private boolean[] mouseButtons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST + 1];
    private boolean isDragging;

    private InputHandler() {
        GLFW.glfwSetCursorPosCallback(GLFW.glfwGetCurrentContext(), (window, xpos, ypos) -> {
            lastMouseX = currentMouseX;
            lastMouseY = currentMouseY;
            currentMouseX = xpos;
            currentMouseY = ypos;
            isDragging = mouseButtons[GLFW.GLFW_MOUSE_BUTTON_LEFT]  ||
                         mouseButtons[GLFW.GLFW_MOUSE_BUTTON_RIGHT] ||
                         mouseButtons[GLFW.GLFW_MOUSE_BUTTON_MIDDLE];
        });
        GLFW.glfwSetScrollCallback(GLFW.glfwGetCurrentContext(), (window, xoffset, yoffset) -> {
            scrollX = xoffset;
            scrollY = yoffset;
        });
        GLFW.glfwSetMouseButtonCallback(GLFW.glfwGetCurrentContext(), (window, button, action, mods) -> {
            mouseButtons[button] = action == GLFW.GLFW_PRESS;
        });
    }
    
    public void eof() {
        scrollX = 0;
        scrollY = 0;
    }

    public double getMouseX() { return currentMouseX; }

    public double getMouseDx() { return currentMouseX - lastMouseX; }

    public double getMouseY() { return currentMouseY; }

    public double getMouseDy() { return currentMouseY - lastMouseY; }

    public double getScroll() { return scrollY; }

    public double getScrollX() { return scrollX; }

    public double getScrollY() { return scrollY; }

    public boolean isMouseButtonUp(int button) { return !mouseButtons[button]; }

    public boolean isMouseButtonDown(int button) { return mouseButtons[button]; }

    public boolean isMouseDragging() { return isDragging; }

}
