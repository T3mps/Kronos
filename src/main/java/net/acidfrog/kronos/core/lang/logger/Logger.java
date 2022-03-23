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
public final class Logger implements LoggerI {

     /** Singleton. */
     public static Logger instance = new Logger(Config.LOG_STREAM, Config.ERROR_STREAM, Config.LOG_TO_FILE, Config.ESCALATION_COLORS);

    /**
     * Determines which {@link LogLevel}s are enabled.
     * 
     * <p>
     * Corresponding levels:
     * 
     * <ul>
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
                                                Config.DEBUG,    // TRACE
                                                !Config.RELEASE, // DEBUG
                                                true,            // INFO
                                                true,            // WARN
                                                true,            // ERROR
                                                true             // FATAL
                                             };

    /** The save path for file output. */
    private static final String SAVE_PATH = Config.LOGGER_SAVE_PATH;

    /** File extension for log files. */
    private static final String EXTENSION = ".log";

    /** Name of this instance. */
    private final String instanceName;

    /** ID of this instance. */
    private final long instanceID;

    /** {@link PrintStream output stream} for messages. */
    private final PrintStream log;

    /** {@link PrintStream output stream} for error messages. */
    private final PrintStream error;

    /** Enables file output. */
    private final boolean logToFile;

    /** Enables escalation colors. */
    private final boolean escalationColors;

    /**
     * Hidden constructor.
     */
    private Logger(final PrintStream log, final PrintStream error, final boolean logToFile, final boolean escalationColors) {
        this.instanceName = LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        this.instanceID = System.currentTimeMillis();
        this.log = log;
        this.error = error;
        this.logToFile = logToFile;
        this.escalationColors = escalationColors;

        if (!initialize()) logError("Failed to initialize logger.");
    }

    /**
     * @see LoggerI#initialize()
     */
    @Override
    public boolean initialize() {
        logToFile(LogLevel.INFO, "Logger(" + instanceID + ") initialized @ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss")));
        return true;
    }

    /**
     *  Internal method for file output.
     */
    private boolean logToFile(final LogLevel level, final String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String fileName = instanceName + EXTENSION;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_PATH + fileName, true))) {
            writer.append("[" + sdf.format(new Date()) + "]" + level.getPrepend() + message + "\n");
            writer.close();
        } catch (IOException e) {
            logError("Failed to write to log file: " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * @see LoggerI#log(LogLevel, String)
     */
    @Override
    public void log(final LogLevel level, final String message) {
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
    public void logAll(final String message) {
        if (LogLevel.ALL.isEnabled()) log(LogLevel.ALL, message);
    }

    /**
     * Logs a message with level {@link LogLevel#TRACE TRACE}.
     * 
     * @param message the {@link String} to log.
     */
    public void logTrace(final String message) {
        if (LogLevel.TRACE.isEnabled()) log(LogLevel.TRACE, message);
    }

    /**
     * Logs a message with level {@link LogLevel#DEBUG DEBUG}.
     * 
     * @param message the {@link String} to log.
     */
    public void logDebug(final String message) {
        if (LogLevel.DEBUG.isEnabled()) log(LogLevel.DEBUG, message);
    }

    /**
     * Logs a message with level {@link LogLevel#INFO INFO}.
     * 
     * @param message the {@link String} to log.
     */
    public void logInfo(final String message) {
        log(LogLevel.INFO, message);
    }

    /**
     * Logs a message with level {@link LogLevel#WARN WARN}.
     * 
     * @param message the {@link String} to log.
     */
    public void logWarn(final String message) {
        log(LogLevel.WARN, message);
    }

    /**
     * Logs a message with level {@link LogLevel#ERROR ERROR}.
     * 
     * @param message the {@link String} to log.
     */
    public void logError(final String message) {
        log(LogLevel.ERROR, message);
    }

    /**
     * Logs a message with level {@link LogLevel#FATAL FATAL}.
     * 
     * @param message the {@link String} to log.
     */
    public void logFatal(final String message) {
        log(LogLevel.FATAL, message);
    }

    /**
     * @see LoggerI#close(int)
     */
    @Override
    public void close(int n) {
        synchronized(this) {
            log.close();
            error.close();
        }
        
        if (logToFile) logToFile(LogLevel.INFO, "Logger(" + instanceID + ") { " + n + " } closed @ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss")));
    }

}
