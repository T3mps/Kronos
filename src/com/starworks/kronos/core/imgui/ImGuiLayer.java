package com.starworks.kronos.core.imgui;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.lwjgl.glfw.GLFW;

import com.starworks.kronos.core.Application;
import com.starworks.kronos.core.Layer;
import com.starworks.kronos.event.Event;
import com.starworks.kronos.files.FileSystem;
import com.starworks.kronos.logging.Logger;
import com.starworks.kronos.resources.ResourceManager;
import com.starworks.kronos.toolkit.SystemInfo;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

public class ImGuiLayer extends Layer {
	private final Logger LOGGER = Logger.getLogger(ImGuiLayer.class);

	private final ImGuiImplGlfw m_imguiImplGlfw;
	private final ImGuiImplGl3 m_imguiImplGl3;
	private ImGuiIO m_io;
	private boolean m_consumeEvents;

	public ImGuiLayer() {
		this.m_imguiImplGlfw = new ImGuiImplGlfw();
		this.m_imguiImplGl3 = new ImGuiImplGl3();
		this.m_io = null;
		this.m_consumeEvents = true;
	}

	@Override
	public void onAttach() {
		ImGui.createContext();
		LOGGER.debug("Created Dear ImGui context");

		m_io = ImGui.getIO();
		Path dirPath = Paths.get(FileSystem.INSTANCE.get("data"));
		if (!Files.exists(dirPath)) {
		    try {
		        Files.createDirectories(dirPath);
		    } catch (IOException e) {
		    	LOGGER.error("Unsuccessfully attempted to generated data folder");
		    }
		}

		m_io.setIniFilename(dirPath.resolve("imgui.ini").toString());
		
		m_io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
		m_io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
		m_io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);

		float fontSize = 18;
		m_io.getFonts().addFontFromFileTTF(ResourceManager.INSTANCE.fetchLocalResourcePath("fonts/opensans/OpenSans-Bold.ttf"), fontSize);
		m_io.setFontDefault(m_io.getFonts().addFontFromFileTTF(ResourceManager.INSTANCE.fetchLocalResourcePath("fonts/opensans/OpenSans-Regular.ttf"), fontSize));

		ImGui.styleColorsDark();

		ImGuiStyle style = ImGui.getStyle();
		if ((m_io.getConfigFlags() & ImGuiConfigFlags.ViewportsEnable) > 0) {
			style.setWindowRounding(0f);
			style.setColor(ImGuiCol.WindowBg, 1);
		}

		m_imguiImplGlfw.init(Application.get().getWindow().getWindowPointer(), true);

		String glslVersion = "#version 330";
		if (SystemInfo.getOSName().contains("mac") || SystemInfo.getOSName().contains("darwin")) {
			glslVersion = "#version 150";
		}

		m_imguiImplGl3.init(glslVersion);
	}

	@Override
	public void onDetach() {
		m_imguiImplGlfw.dispose();
		m_imguiImplGl3.dispose();
		ImGui.destroyContext();
	}

	@Override
	public boolean onEvent(Event event) {
		if (m_consumeEvents) {
			m_io = ImGui.getIO();
			return (event.isInCategory(Event.CATEGORY_KEYBOARD) && m_io.getWantCaptureKeyboard()) ||
				   (event.isInCategory(Event.CATEGORY_MOUSE) && m_io.getWantCaptureMouse());
		}
		return false;
	}
	
	public void begin() {
		m_imguiImplGlfw.newFrame();
		ImGui.newFrame();
	}

	public void end() {
		m_io = ImGui.getIO();
		m_io.setDisplaySize(Application.get().getWindow().getWidth(), Application.get().getWindow().getHeight());

		ImGui.render();
		m_imguiImplGl3.renderDrawData(ImGui.getDrawData());

		if (m_io.hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
			long backupCurrentContext = GLFW.glfwGetCurrentContext();
			ImGui.updatePlatformWindows();
			ImGui.renderPlatformWindowsDefault();
			glfwMakeContextCurrent(backupCurrentContext);
		}
	}
	
	public void consumesEvents(boolean flag) {
		m_consumeEvents = flag;
	}
}
