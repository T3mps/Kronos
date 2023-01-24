package com.starworks.kronos;

import com.starworks.kronos.toolkit.XMLMap;

public final class Configuration {

	private static final XMLMap s_map;
	static {
		s_map = new XMLMap("configuration.xml");
		if (s_map.wasGenerated()) {
			s_map.put("applicationImplementation", 	"com.starworks.kronos.sandbox.Sandbox");
			s_map.put("workingDirectory", 			"");
			s_map.put("windowWidth", 				"800");
			s_map.put("windowHeight", 				"600");
			s_map.put("windowTitle", 				"Kronos");
			s_map.put("updatesPerSecond", 			"60");
			s_map.put("fixedUpdatesPerSecond", 		"60");
			s_map.put("debug", 						"true");
			s_map.export();
		}
	}
	
	public static final String APPLICATION_IMPL = s_map.get("applicationImplementation");

	public static final String WORKING_DIRECTORY = s_map.get("workingDirectory");

	public static final int WINDOW_WIDTH = Integer.parseInt(s_map.get("windowWidth"));

	public static final int WINDOW_HEIGHT = Integer.parseInt(s_map.get("windowHeight"));

	public static final String WINDOW_TITLE = s_map.get("windowTitle");

	public static final double UPDATES_PER_SECOND = Double.parseDouble(s_map.get("updatesPerSecond"));

	public static final double UPDATE_RATE = 1 / UPDATES_PER_SECOND;

	public static final double FIXED_UPDATES_PER_SECOND = Double.parseDouble(s_map.get("fixedUpdatesPerSecond"));

	public static final double FIXED_UPDATE_RATE = 1 / FIXED_UPDATES_PER_SECOND;

	public static final boolean DEBUG = s_map.get("updatesPerSecond").equalsIgnoreCase("true");

	
	private Configuration() {}
}
