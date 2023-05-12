package com.starworks.kronos.core;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.nio.IntBuffer;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import com.starworks.kronos.event.Event;
import com.starworks.kronos.exception.Exceptions;
import com.starworks.kronos.exception.KronosRuntimeException;
import com.starworks.kronos.logging.Logger;
import com.starworks.kronos.rendering.platform.OpenGLContext;
import com.starworks.kronos.rendering.platform.RenderingContext;

public final class Window implements AutoCloseable {
	private static final Logger LOGGER = Logger.getLogger(Window.class);

	private int m_width;
	private int m_height;
	private String m_title;
	private boolean m_vsync;
	private final Lock m_lock;
	private long m_windowPointer;
	private RenderingContext m_context;
	private Consumer<Event> m_onEvent;

	private Window(int width, int height, String title) {
		this.m_width = width;
		this.m_height = height;
		this.m_title = title;
		this.m_lock = new ReentrantLock();
		this.m_onEvent = null;
	}

	public static Window create(int width, int height, String title) {
		if (width <= 0) throw new KronosRuntimeException(Exceptions.getMessage("core.window.invalidWidth"));
		if (height <= 0) throw new KronosRuntimeException(Exceptions.getMessage("core.window.invalidHeight"));
		if (title == null) throw new KronosRuntimeException(Exceptions.getMessage("core.window.invalidTitle"));
		Window window = new Window(width, height, title);
		LOGGER.info("Window[{2}] created; width: {0}, height: {1}", width, height, title);
		window.initialize();
		return window;
	}

	private void initialize() {
		GLFWErrorCallback callback = GLFWErrorCallback.create((int error, long description) -> {
		    LOGGER.error("GLFW error [" + error + "]: " + GLFWErrorCallback.getDescription(description));
		});
		glfwSetErrorCallback(callback);
		if (!glfwInit()) {
			throw new KronosRuntimeException(Exceptions.getMessage("core.window.initializationFailed"));
		}
		LOGGER.info("GLFW initialized");
		
		setWindowHints();
		LOGGER.info("Window[{0}] hints set", m_title);

		m_windowPointer = GLFW.glfwCreateWindow(m_width, m_height, m_title, MemoryUtil.NULL, MemoryUtil.NULL);
		if (m_windowPointer == MemoryUtil.NULL) {
			throw new KronosRuntimeException(Exceptions.getMessage("core.window.creationFailed"));
		}

		m_context = new OpenGLContext(m_windowPointer);
		m_context.initialize();

		LOGGER.info("Window[{0}] assigned to pointer '{1}L'", m_title, m_windowPointer);

		setWindowPosition();
		LOGGER.info("Window[{0}] centered", m_title);

		setVSync(true);

		GLFW.glfwShowWindow(m_windowPointer);
		LOGGER.info("Window[{0}] shown", m_title);
	}

	public Window onEvent(Consumer<Event> onEvent) {
		if (onEvent == null) {
			throw new KronosRuntimeException(Exceptions.getMessage("core.window.invalidEventCallback"));
		}
		m_onEvent = onEvent;
		registerEvents();
		return this;
	}

	private void registerEvents() {
		GLFW.glfwSetKeyCallback(m_windowPointer, (window, key, scancode, action, mods) -> {
			m_onEvent.accept(switch (action) {
			case GLFW.GLFW_PRESS -> new Event.KeyPressed(key, scancode, mods, System.nanoTime());
			case GLFW.GLFW_RELEASE -> new Event.KeyReleased(key, scancode, mods, System.nanoTime());
			case GLFW.GLFW_REPEAT -> new Event.KeyRepeated(key, scancode, mods, System.nanoTime());
			default -> throw new IllegalArgumentException(Exceptions.getMessage("core.window.invalidEvent"));
			});
		});
		GLFW.glfwSetMouseButtonCallback(m_windowPointer, (window, button, action, mods) -> {
			m_onEvent.accept(switch (action) {
			case GLFW.GLFW_PRESS -> new Event.MouseButtonPressed(button, mods, System.nanoTime());
			case GLFW.GLFW_RELEASE -> new Event.MouseButtonReleased(button, mods, System.nanoTime());
			default -> throw new IllegalArgumentException(Exceptions.getMessage("core.window.invalidEvent"));
			});
		});
		GLFW.glfwSetCursorPosCallback(m_windowPointer, (window, xpos, ypos) -> {
			m_onEvent.accept(new Event.MouseMoved(xpos, ypos, System.nanoTime()));
		});
		GLFW.glfwSetScrollCallback(m_windowPointer, (window, xoffset, yoffset) -> {
			m_onEvent.accept(new Event.MouseScrolled(xoffset, yoffset, System.nanoTime()));
		});
		GLFW.glfwSetWindowSizeCallback(m_windowPointer, (window, width, height) -> {
			m_onEvent.accept(new Event.WindowResized(width, height, System.nanoTime()));
		});
		GLFW.glfwSetWindowCloseCallback(m_windowPointer, (window) -> {
			m_onEvent.accept(new Event.WindowClosed(System.nanoTime()));
		});
		GLFW.glfwSetWindowPosCallback(m_windowPointer, (window, xpos, ypos) -> {
			m_onEvent.accept(new Event.WindowMoved(xpos, ypos, System.nanoTime()));
		});
		GLFW.glfwSetWindowFocusCallback(m_windowPointer, (window, focused) -> {
			if (focused) {
				m_onEvent.accept(new Event.WindowFocusGained(System.nanoTime()));
			} else {
				m_onEvent.accept(new Event.WindowFocusLost(System.nanoTime()));
			}
		});
		GLFW.glfwSetWindowIconifyCallback(m_windowPointer, (window, iconified) -> {
			if (iconified) {
				m_onEvent.accept(new Event.WindowMinimized(System.nanoTime()));
			} else {
				m_onEvent.accept(new Event.WindowRestored(System.nanoTime()));
			}
		});
		GLFW.glfwSetWindowMaximizeCallback(m_windowPointer, (window, maximized) -> {
			if (maximized) {
				m_onEvent.accept(new Event.WindowMaximized(System.nanoTime()));
			} else {
				m_onEvent.accept(new Event.WindowUnmaximized(System.nanoTime()));
			}
		});
		GLFW.glfwSetWindowRefreshCallback(m_windowPointer, (window) -> {
			m_onEvent.accept(new Event.WindowRefreshed(System.nanoTime()));
		});
	}

	public void update() {
		GLFW.glfwPollEvents();
		m_context.swapBuffers();
	}

	public boolean shouldClose() {
		return GLFW.glfwWindowShouldClose(m_windowPointer);
	}

	private void setWindowHints() {
		GLFW.glfwDefaultWindowHints();
		LOGGER.debug("Set default GLFW window hints");
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		LOGGER.debug("Set GLFW window hint 'GLFW_VISIBLE' to `GLFW_FALSE`");
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
		LOGGER.debug("Set GLFW window hint 'GLFW_RESIZABLE' to `GLFW_TRUE`");

		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		LOGGER.debug("Set GLFW window hint 'GLFW_CONTEXT_VERSION_MAJOR' to `{0}`", 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 0);
		LOGGER.debug("Set GLFW window hint 'GLFW_CONTEXT_VERSION_MINOR' to `{0}`", 0);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
		LOGGER.debug("Set GLFW window hint 'GLFW_OPENGL_FORWARD_COMPAT' to `GLFW_TRUE`");
	}

	private void setWindowPosition() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);

			GLFW.glfwGetWindowSize(m_windowPointer, pWidth, pHeight);

			GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

			GLFW.glfwSetWindowPos(m_windowPointer, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
		}
	}

	@Override
	public void close() {
		m_lock.lock();
		try {
			Callbacks.glfwFreeCallbacks(m_windowPointer);
			GLFW.glfwDestroyWindow(m_windowPointer);
			glfwTerminate();
			Objects.requireNonNull(glfwSetErrorCallback(null)).free();
			LOGGER.info("Window[{0}] closed", m_title);
			LOGGER.close();
		} finally {
			m_lock.unlock();
		}
	}

	public int getWidth() {
		return m_width;
	}

	public int getHeight() {
		return m_height;
	}

	public Window setVSync(boolean vsync) {
		GLFW.glfwSwapInterval((m_vsync = vsync) ? 1 : 0);
		LOGGER.info("Window[{0}] vsync {1}", m_title, vsync ? "enabled" : "disabled");
		return this;
	}

	public boolean isVSync() {
		return m_vsync;
	}

	public long getWindowPointer() {
		return m_windowPointer;
	}
}
