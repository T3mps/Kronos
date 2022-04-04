package net.acidfrog.kronos.core.architecture;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import net.acidfrog.kronos.core.Config;
import net.acidfrog.kronos.core.lang.error.KronosError;
import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;
import net.acidfrog.kronos.core.lang.logger.Logger;
import net.acidfrog.kronos.math.Vector4f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.IntBuffer;

public final class Window {

    private int width;
    private int height;
    private String title;
    private long pointer;

    public Window() {
        this(800, 600, "Kronos");
    }

    public Window(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public Window vsync(boolean vsync) {
        glfwSwapInterval(vsync ? 1 : 0);
        return this;
    }
    
    public Window setClearColor(Vector4f color) {
        GL11.glClearColor(color.x, color.y, color.z, color.w);
        return this;
    }

    public Window initialize() {
        GLFWErrorCallback.createPrint(Logger.getErrorStream()).set();

        if (!glfwInit()) throw new KronosError(KronosErrorLibrary.GLFW_INIT_FAILED);

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        if (Config.OPERATING_SYSTEM == Config.OSArbiter.MAC) glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        pointer = glfwCreateWindow(width, height, title, NULL, NULL);
        if (pointer == NULL) throw new KronosError(KronosErrorLibrary.GLFW_WINDOW_CREATION_FAILED);
        
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(pointer, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(pointer, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
        }

        glfwMakeContextCurrent(pointer);
        glfwSwapInterval(1);
        glfwShowWindow(pointer);

        GL.createCapabilities();
        return this;
    }

    public long pointer() {
        return pointer;
    }
    
}
