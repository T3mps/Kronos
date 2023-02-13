package com.starworks.kronos.core;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCharModsCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowFocusCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIconifyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMaximizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowRefreshCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;

import java.nio.IntBuffer;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import com.starworks.kronos.event.Event;
import com.starworks.kronos.exception.Exceptions;
import com.starworks.kronos.exception.KronosRuntimeException;
import com.starworks.kronos.logging.Logger;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

public final class Window implements AutoCloseable {
	private static final Logger LOGGER = Logger.getLogger(Window.class);

	private int m_width;
	private int m_height;
	private String m_title;
	private boolean m_vsync;
	private final Lock m_updateLock;
	private final Lock m_destructionLock;
	private long m_pointer;
	private Consumer<Event> m_onEvent;
	
	private final ImGuiImplGlfw m_imguiImplGlfw;
	private final ImGuiImplGl3 m_imguiImplGl3;

	private final Deque<WindowLayer> m_layers;

	private Window(int width, int height, String title) {
		this.m_width = width;
		this.m_height = height;
		this.m_title = title;
		this.m_updateLock = new ReentrantLock();
		this.m_destructionLock = new ReentrantLock();
		this.m_onEvent = null;
		this.m_imguiImplGlfw = new ImGuiImplGlfw();
		this.m_imguiImplGl3 = new ImGuiImplGl3();
		this.m_layers = new ConcurrentLinkedDeque<WindowLayer>();
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
	
	public Window onEvent(Consumer<Event> onEvent) {
		if (onEvent == null) {
			throw new KronosRuntimeException(Exceptions.getMessage("core.window.invalidEventCallback"));
		}
		m_onEvent = onEvent;
		registerEvents();
		return this;
	}

	public Window addWindowLayer(WindowLayer layer) {
		if (layer == null) {
			throw new KronosRuntimeException(Exceptions.getMessage("core.window.invalidLayer"));
		}
		m_layers.add(layer);
		return this;
	}

	private void initialize() {
		GLFWContext.init();
		LOGGER.info("GLFWContext initialized");
		setWindowHints();
		LOGGER.info("Window[{0}] hints set", m_title);

		m_pointer = glfwCreateWindow(m_width, m_height, m_title, NULL, NULL);
		if (m_pointer == NULL) {
			throw new KronosRuntimeException(Exceptions.getMessage("core.window.creationFailed"));
		}
		LOGGER.info("Window[{0}] assigned to pointer '{1}L'", m_title, m_pointer);

		setWindowPosition();
		LOGGER.info("Window[{0}] centered", m_title);

		glfwMakeContextCurrent(m_pointer);
		LOGGER.info("Window[{0}] context made current", m_title);
		setVSync(true);

		createCapabilities();

		initializeImGui();

		glfwShowWindow(m_pointer);
		LOGGER.info("Window[{0}] shown", m_title);
	}
	
	private void registerEvents() {
		glfwSetKeyCallback(m_pointer, (window, key, scancode, action, mods) -> {
			m_onEvent.accept(switch (action) {
			case GLFW_PRESS -> new Event.KeyPressed(key, scancode, mods, System.nanoTime());
			case GLFW_RELEASE -> new Event.KeyReleased(key, scancode, mods, System.nanoTime());
			case GLFW_REPEAT -> new Event.KeyRepeated(key, scancode, mods, System.nanoTime());
			default -> throw new IllegalArgumentException(Exceptions.getMessage("core.window.invalidEvent"));
			});
		});
		glfwSetCharModsCallback(m_pointer, (window, codepoint, mods) -> {
			m_onEvent.accept(new Event.KeyTyped(codepoint, mods, System.nanoTime()));
		});
		glfwSetMouseButtonCallback(m_pointer, (window, button, action, mods) -> {
			m_onEvent.accept(switch (action) {
			case GLFW_PRESS -> new Event.MouseButtonPressed(button, mods, System.nanoTime());
			case GLFW_RELEASE -> new Event.MouseButtonReleased(button, mods, System.nanoTime());
			default -> throw new IllegalArgumentException(Exceptions.getMessage("core.window.invalidEvent"));
			});
		});
		glfwSetCursorPosCallback(m_pointer, (window, xpos, ypos) -> {
			m_onEvent.accept(new Event.MouseMoved(xpos, ypos, System.nanoTime()));
		});
		glfwSetScrollCallback(m_pointer, (window, xoffset, yoffset) -> {
			m_onEvent.accept(new Event.MouseWheel(xoffset, yoffset, System.nanoTime()));
		});
		glfwSetWindowSizeCallback(m_pointer, (window, width, height) -> {
			m_onEvent.accept(new Event.WindowResized(width, height, System.nanoTime()));
		});
		glfwSetWindowCloseCallback(m_pointer, (window) -> {
			m_onEvent.accept(new Event.WindowClosed(System.nanoTime()));
		});
		glfwSetWindowPosCallback(m_pointer, (window, xpos, ypos) -> {
			m_onEvent.accept(new Event.WindowMoved(xpos, ypos, System.nanoTime()));
		});
		glfwSetWindowFocusCallback(m_pointer, (window, focused) -> {
			if (focused) {
				m_onEvent.accept(new Event.WindowFocusGained(System.nanoTime()));
			} else {
				m_onEvent.accept(new Event.WindowFocusLost(System.nanoTime()));
			}
		});
		glfwSetWindowIconifyCallback(m_pointer, (window, iconified) -> {
			if (iconified) {
				m_onEvent.accept(new Event.WindowMinimized(System.nanoTime()));
			} else {
				m_onEvent.accept(new Event.WindowRestored(System.nanoTime()));
			}
		});
		glfwSetWindowMaximizeCallback(m_pointer, (window, maximized) -> {
			if (maximized) {
				m_onEvent.accept(new Event.WindowMaximized(System.nanoTime()));
			} else {
				m_onEvent.accept(new Event.WindowUnmaximized(System.nanoTime()));
			}
		});
		glfwSetWindowRefreshCallback(m_pointer, (window) -> {
			m_onEvent.accept(new Event.WindowRefreshed(System.nanoTime()));
		});
	}

	public void update() {
		m_updateLock.lock();
		try {
			glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			m_imguiImplGlfw.newFrame();
			ImGui.newFrame();

			m_layers.forEach(WindowLayer::onUpdate);
			
			ImGui.render();
			m_imguiImplGl3.renderDrawData(ImGui.getDrawData());
			
			if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
				long backupCurrentContext = GLFW.glfwGetCurrentContext();
				ImGui.updatePlatformWindows();
				ImGui.renderPlatformWindowsDefault();
				glfwMakeContextCurrent(backupCurrentContext);
			}

			glfwSwapBuffers(m_pointer);
			glfwPollEvents();
		} finally {
			m_updateLock.unlock();
		}
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(m_pointer);
	}

	private void setWindowHints() {
		glfwDefaultWindowHints();
		LOGGER.debug("Set default GLFW window hints");
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		LOGGER.debug("Set GLFW window hint 'GLFW_VISIBLE' to `GLFW_FALSE`");
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		LOGGER.debug("Set GLFW window hint 'GLFW_RESIZABLE' to `GLFW_TRUE`");
		
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		LOGGER.debug("Set GLFW window hint 'GLFW_CONTEXT_VERSION_MAJOR' to `{0}`", 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
		LOGGER.debug("Set GLFW window hint 'GLFW_CONTEXT_VERSION_MINOR' to `{0}`", 0);
//		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
//		LOGGER.debug("Set GLFW window hint 'GLFW_OPENGL_PROFILE' to `GLFW_OPENGL_CORE_PROFILE`");
//		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
//		LOGGER.debug("Set GLFW window hint 'GLFW_OPENGL_FORWARD_COMPAT' to `GLFW_TRUE`");
	}

	private void setWindowPosition() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);

			glfwGetWindowSize(m_pointer, pWidth, pHeight);

			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			glfwSetWindowPos(m_pointer, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
		}
	}

	private void initializeImGui() {
		// Setup Dear ImGui context
		ImGui.createContext();
//		ImGuiIO io = ImGui.getIO();
		LOGGER.debug("Created Dear ImGui context");
		
		// Setup Dear ImGui style
		ImGui.styleColorsDark();
		// ImGui.styleColorsClassic();
		
		m_imguiImplGlfw.init(m_pointer, true);

		String glslVersion = "#version 130";
//		if (SystemInfo.getOSName().contains("mac") || SystemInfo.getOSName().contains("darwin")) {
//			glslVersion = "#version 150";
//		} else {
//			glslVersion = "#version 330";
//		}

		m_imguiImplGl3.init(glslVersion);
	}

	@Override
	public void close() {
		m_destructionLock.lock();
		try {
			m_imguiImplGlfw.dispose();
			m_imguiImplGl3.dispose();
			ImGui.destroyContext();
			glfwFreeCallbacks(m_pointer);
			glfwDestroyWindow(m_pointer);
			GLFWContext.terminate();
			LOGGER.info("Window[{0}] closed", m_title);
			LOGGER.close();
		} finally {
			m_destructionLock.unlock();
		}
	}

	public Window setVSync(boolean vsync) {
		glfwSwapInterval((m_vsync = vsync) ? 1 : 0);
		LOGGER.info("Window[{0}] vsync {1}", m_title, vsync ? "enabled" : "disabled");
		return this;
	}

	public boolean isVSync() {
		return m_vsync;
	}

	@FunctionalInterface
	public interface WindowLayer {

		void onUpdate();
	}
}
