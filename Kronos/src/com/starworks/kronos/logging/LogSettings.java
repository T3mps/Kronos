package com.starworks.kronos.logging;

public final class LogSettings {

    /** The default name of a {@link Logger}. */
    public static String DEFAULT_LOGGER_NAME = "main";
    
    /** The default level of a {@link Logger}. */
    public static Level DEFAULT_LOGGER_LEVEL = Level.DEBUG;
    
    /** The default {@link Logger} implementation. */
    public static Class<? extends Logger> DEFAULT_LOGGER_TYPE = SimpleLogger.class;
    
    /** The directory targeted for file output. */
    public static String FILE_OUTPUT_DIRECTORY = "Scribe/logs/";

    /** The file extension associated with log files. */
    public static String FILE_EXTENSION = ".log";

    /** The date format used for file output. */
    public static String FILE_DATE_FORMAT = "yyyy-MM-dd";

    /** The time format used for logging messages. */
    public static String MESSAGE_TIME_FORMAT = "HH:mm:ss.SSS";

}
