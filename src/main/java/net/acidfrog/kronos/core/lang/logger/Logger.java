package net.acidfrog.kronos.core.lang.logger;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import net.acidfrog.kronos.core.Config;

/**
 * Class for logging messages to console and file.
 * 
 * @author Ethan Temprovich
 */
public final class Logger {

    /**
     * Determines which {@link LogLevel}s are enabled.
     * 
     * <p>
     * Corresponding levels:
     * 
     * <ul>
     * <li>{@link LogLevel#ALL}</li>
     * <li>{@link LogLevel#TRACE}</li>
     * <li>{@link LogLevel#DEBUG}</li>
     * <li>{@link LogLevel#INFO}</li>
     * <li>{@link LogLevel#WARN}</li>
     * <li>{@link LogLevel#ERROR}</li>
     * <li>{@link LogLevel#FATAL}</li>
     * </ul>
     */
    static final boolean LEVEL_PREDICATE[] = {
                                                true,            // ALL
                                                Config.getInstance().getBoolean("kronos.debug"),    // TRACE
                                                !Config.getInstance().getBoolean("kronos.release"), // DEBUG
                                                true,            // INFO
                                                true,            // WARN
                                                true,            // ERROR
                                                true             // FATAL
                                             };

    /** The save path for file output. */
    private static final String SAVE_PATH = Config.getInstance().getString("kronos.loggerSavePath");

    /** File extension for log files. */
    private static final String EXTENSION = ".log";

    /** Name of this instance. */
    private static String instanceName = LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));

    /** ID of this instance. */
    private static long instanceID = System.currentTimeMillis();

    /** {@link PrintStream output stream} for messages. */
    private static PrintStream log = System.out;

    /** {@link PrintStream output stream} for error messages. */
    private static PrintStream error = System.err;

    /** Enables file output. */
    private static boolean logToFile = Config.getInstance().getBoolean("kronos.logToFile");

    /** Enables escalation colors. */
    private static boolean escalationColors = Config.getInstance().getBoolean("kronos.escalationColors");

    /**
     * @see LoggerI#initialize()
     */
    public static boolean initialize() {
        logToFile(LogLevel.INFO, "Logger(" + instanceID + ") initialized @ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss")));
        return true;
    }

    /**
     *  Internal method for file output.
     */
    private static boolean logToFile(final LogLevel level, final String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String fileName = instanceName + EXTENSION;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_PATH + fileName, true))) {
            writer.append("[" + sdf.format(new Date()) + "]" + level.getPrepend() + message + "\n");
            writer.close();
        } catch (IOException e) {
            error.println("Failed to write to log file: '" + e.getMessage().substring(0, e.getMessage().indexOf(" (")) + "'" + e.getMessage().substring(e.getMessage().indexOf(" (")));
            return false;
        }

        return true;
    }

    public static void log(final LogLevel level, final String message) {
        boolean isError = level.ordinal() >= 4;
        String output = level.getPrepend() + message;
        output = escalationColors ? Ansi.colorize(output, level.getForegroundColor(), level.getBackgroundColor()) : output;
        if (logToFile) logToFile(level, message);
        (isError ? error : log).println(output);
    }
    
    /**
     * Logs a message with level {@link LogLevel#ALL ALL}.
     * 
     * @param message the {@link String} to log.
     */
    public static void logAll(final String message) {
        if (LogLevel.ALL.isEnabled()) log(LogLevel.ALL, message);
    }

    /**
     * Logs a message with level {@link LogLevel#TRACE TRACE}.
     * 
     * @param message the {@link String} to log.
     */
    public static void logTrace(final String message) {
        if (LogLevel.TRACE.isEnabled()) log(LogLevel.TRACE, message);
    }

    /**
     * Logs a message with level {@link LogLevel#DEBUG DEBUG}.
     * 
     * @param message the {@link String} to log.
     */
    public static void logDebug(final String message) {
        if (LogLevel.DEBUG.isEnabled()) log(LogLevel.DEBUG, message);
    }

    /**
     * Logs a message with level {@link LogLevel#INFO INFO}.
     * 
     * @param message the {@link String} to log.
     */
    public static void logInfo(final String message) {
        log(LogLevel.INFO, message);
    }

    /**
     * Logs a message with level {@link LogLevel#WARN WARN}.
     * 
     * @param message the {@link String} to log.
     */
    public static void logWarn(final String message) {
        log(LogLevel.WARN, message);
    }

    /**
     * Logs a message with level {@link LogLevel#ERROR ERROR}.
     * 
     * @param message the {@link String} to log.
     */
    public static void logError(final String message) {
        log(LogLevel.ERROR, message);
    }

    /**
     * Logs a message with level {@link LogLevel#FATAL FATAL}.
     * 
     * @param message the {@link String} to log.
     */
    public static void logFatal(final String message) {
        log(LogLevel.FATAL, message);
    }

    public static void close(int n) {
        synchronized(Logger.class) {
            log.close();
            error.close();
        }
        
        if (logToFile) logToFile(LogLevel.INFO, "Logger(" + instanceID + ") { " + n + " } closed @ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss")));
    }

    public static PrintStream getOutputStream() {
        return log;
    }

    public static PrintStream getErrorStream() {
        return error;
    }

}
