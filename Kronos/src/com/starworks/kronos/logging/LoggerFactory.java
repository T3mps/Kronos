package com.starworks.kronos.logging;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to either retrieve a cached {@link Logger logger} or create
 * a new one.
 * <p>
 * Using {@link #get(String, Class)} or {@link #get(String, Class, Logger)} will
 * return a {@link Logger logger} with the given name and class. If the
 * {@link Logger logger} has not been created, it will be created and returned.
 * <p>
 * The {@link Logger logger} is instantiated using Java Reflection. This means that
 * the {@link #loggerClass} is swapable to any implementation via
 * {@link #setLoggerClass(Class)}.
 * 
 * @author Ethan Temprovich
 * @version 1.0
 * @since 1.0
 * @see Logger
 * @see SimpleLogger
 */
public final class LoggerFactory {

    /** List of all {@link Logger loggers} created. */
    static final List<Logger> loggers = new ArrayList<Logger>();

    /** The implementation of the {@link Logger} interface used to reflect. */
    private static Class<? extends Logger> loggerClass = getLoggerClass();

    /**
     * Hidden constructor.
     */
    private LoggerFactory() {}

    public static Logger get(Class<?> clazz) {
        return get(LogSettings.DEFAULT_LOGGER_NAME, clazz);
    }
    
    /**
     * Returns a {@link Logger logger} with the given name and class. If the
     * {@link Logger logger} has not been created, it will be created and returned.
     * 
     * @param name
     * @param clazz
     * @return {@link Logger logger}
     */
    
    public static Logger get(String name, Class<?> clazz) {
        return get(name, clazz, null);
    }

    /**
     * Returns a {@link Logger logger} with the given name, class, and parent. If the
     * {@link Logger logger} has not been created, it will be created and returned.
     * 
     * @param name
     * @param clazz
     * @param parent
     * @return {@link Logger logger}
     */
    public static Logger get(String name, Class<?> clazz, Logger parent) {
        if (name == null) {
            name = LogSettings.DEFAULT_LOGGER_NAME;
        }
        if (clazz == null) {
            throw new IllegalArgumentException("Class must not be null.");
        }

        synchronized (loggers) {
            for (var logger : loggers) {
                if (logger.getName().equals(name) && logger.getLoggedClass().equals(clazz) && logger.getParent() == parent) {
                    return logger;
                }
            }

            try {
                var logger = loggerClass.getConstructor(String.class, Class.class, loggerClass).newInstance(name, clazz, parent)
                .setLevel(LogSettings.DEFAULT_LOGGER_LEVEL);
                loggers.add(logger);
                return logger;
            } catch (Exception e) {
                throw new RuntimeException("Failed to create logger", e);
            }
        }
    }

    /**
     * @return The {@link #loggerClass} used to create {@link Logger loggers}.
     */
    private static final Class<? extends Logger> getLoggerClass() {
        if (loggerClass == null) {
            loggerClass = LogSettings.DEFAULT_LOGGER_TYPE;
        }
        return loggerClass;
    }

    /**
     * Sets the {@link #loggerClass} used to create {@link Logger loggers}.
     * 
     * @param loggerClass
     */
    public static final void setLoggerClass(Class<? extends Logger> loggerClass) {
        LoggerFactory.loggerClass = loggerClass;
    }
}
