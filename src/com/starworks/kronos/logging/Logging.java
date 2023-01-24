package com.starworks.kronos.logging;

import com.starworks.kronos.toolkit.XMLMap;

public final class Logging {

	private static final XMLMap s_map;
	static {
		s_map = new XMLMap("logging.xml");
		if (s_map.wasGenerated()) {
			s_map.put("loggerImplementation", 		"com.starworks.kronos.logging.ConcurrentLogger");
			s_map.put("logToConsole", 				"true");
			s_map.put("logToFile", 					"true");
			s_map.put("maxRotatingFileLineCount", 	"10000");
			s_map.put("directory", 					"logs/");
			s_map.put("defaultName", 				"Main");
			s_map.put("defaultLevel", 				"all");
			s_map.put("ansiFormatting", 			"true");
			s_map.put("layout", 					"full");
			s_map.put("logFileExtension", 			".log");
			s_map.put("logFileBackupExtension", 	".bak");
			s_map.export();
		}
	}

	public static final String  LOGGER_IMPLEMENTATION 		=	 s_map.get("loggerImplementation");
	public static final boolean LOG_TO_CONSOLE 				=	 s_map.get("logToConsole").equalsIgnoreCase("true");
	public static final boolean LOG_TO_FILE 				=	 s_map.get("logToFile").equalsIgnoreCase("true");
	public static final int 	MAX_ROTATING_FILE_LINES 	=	 Integer.parseInt(s_map.get("maxRotatingFileLineCount"));
	public static final String  DIRECTORY 					=	 s_map.get("directory");
	public static final String  DEFAULT_NAME 				=	 s_map.get("defaultName");
	public static final Level 	DEFAULT_LEVEL 				=	 Level.of(s_map.get("defaultLevel"));
	public static final boolean DEFAULT_ANSI_FLAG 			=	 s_map.get("ansiFormatting").equalsIgnoreCase("true");
	public static final Layout 	DEFAULT_LAYOUT 				=	 Layout.of(s_map.get("layout"));
	public static final String 	EXTENSION 					=	 s_map.get("logFileExtension");
	public static final String 	BACKUP_EXTENSION 			=	 s_map.get("logFileBackupExtension");
}
