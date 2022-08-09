package net.acidfrog.kronos.scribe;

import java.io.Closeable;
import java.util.List;

import net.acidfrog.kronos.crates.Ownable;
import net.acidfrog.kronos.scribe.callbacks.LogCallback;

/**
 * Interface which outlines the capabilities of a logger. All creation of any
 * logger should be done through the {@link LoggerFactory} class.
 * <p>
 * The paradigm which a logger embodies includes the ability to log messages of
 * different {@link Level levels}, which define the severity of the message. A
 * logger also should have control over which {@link Level levels} are listened
 * to, and which are ignored.
 * <p>
 * {@link LoggerListener Logger listeners} can be registered to loggers.
 * When a logger is modified, it should notify these listeners of the change.
 * <p>
 * Finally, a logger needs to be able to log messages to a destination, these
 * are defined as {@link LogCallback log callbacks}. Log callbacks included
 * in the standard logging framework are the {@link ConsoleLogCallback} and
 * {@link FileLogCallback}.
 * 
 * @author Ethan Temprovich
 * @version 1.0
 * @since 1.0
 * @see SimpleLogger
 */
public interface Logger extends Ownable<Logger>, Closeable {
 
    /**
     * Adds a child {@link Logger logger} to this logger. Sets the parent of the
     * child to this.
     * 
     * @param child
     * @return this
     */
    public abstract Logger addChild(Logger child);

    /**
     * Returns a child {@link Logger logger} with the given name if it exists.
     * 
     * @param name
     * @return the child logger with the given name, or null if is not present
     */
    public abstract Logger getChild(String name);
    
    /**
     * Determines if this {@link Logger logger} has a specified child instance.
     * 
     * @param child
     * @return true if the child is present, false otherwise
     */
    public abstract boolean hasChild(Logger child);

    /**
     * Determines if this {@link Logger logger} has a child with the given name.
     * 
     * @param name
     * @return true if the child is present, false otherwise
     */
    public abstract boolean hasChild(String name);

    /**
     * Removes the child {@link Logger logger} instance from this logger, if it
     * exists.
     * 
     * @param child
     * @return the removed child, or null if it was not present
     */
    public abstract Logger removeChild(Logger child);

    /**
     * Adds a {@link LogCallback log callback} to this logger.
     * 
     * @param callback
     */
    public abstract void addCallback(LogCallback callback);

    /**
     * Removes a {@link LogCallback log callback} from this logger.
     * 
     * @param callback
     * @return true if the callback was present, false otherwise
     */
    public abstract boolean removeCallback(LogCallback callback);

    /**
     * Registers a {@link LoggerListener logger listener} to this logger.
     * 
     * @param listener
     */
    public abstract void registerListener(LoggerListener listener);

    /**
     * Removes a {@link LoggerListener logger listener} from this logger.
     * 
     * @param listener
     * @return
     */
    public abstract boolean unregisterListener(LoggerListener listener);

    /**
     * Logs a message at the {@link Level#TRACE trace} level.
     * 
     * @param message
     */
    public abstract void trace(String message);

    /**
     * Logs a message at the {@link Level#TRACE trace} level.
     * 
     * @param format
     * @param arg
     */
    public abstract void trace(String format, Object arg);

    /**
     * Logs a message at the {@link Level#TRACE trace} level.
     * 
     * @param format
     * @param args
     */
    public abstract void trace(String format, Object... args);

    /**
     * Logs a message at the {@link Level#TRACE trace} level.
     * 
     * @param message
     * @param t
     */
    public abstract void trace(String message, Throwable t);

    /**
     * Logs a message at the {@link Level#DEBUG debug} level.
     * 
     * @param message
     */
    public abstract void debug(String message);

    /**
     * Logs a message at the {@link Level#DEBUG debug} level.
     * 
     * @param format
     * @param arg
     */
    public abstract void debug(String format, Object arg);

    /**
     * Logs a message at the {@link Level#DEBUG debug} level.
     * 
     * @param format
     * @param args
     */
    public abstract void debug(String format, Object... args);

    /**
     * Logs a message at the {@link Level#DEBUG debug} level.
     * 
     * @param message
     * @param t
     */
    public abstract void debug(String message, Throwable t);

    /**
     * Logs a message at the {@link Level#INFO info} level.
     * 
     * @param message
     */
    public abstract void info(String message);

    /**
     * Logs a message at the {@link Level#INFO info} level.
     * 
     * @param format
     * @param arg
     */
    public abstract void info(String format, Object arg);

    /**
     * Logs a message at the {@link Level#INFO info} level.
     * 
     * @param format
     * @param args
     */
    public abstract void info(String format, Object... args);

    /**
     * Logs a message at the {@link Level#INFO info} level.
     * 
     * @param message
     * @param t
     */
    public abstract void info(String message, Throwable t);

    /**
     * Logs a message at the {@link Level#WARN warn} level.
     * 
     * @param message
     */
    public abstract void warn(String message);

    /**
     * Logs a message at the {@link Level#WARN warn} level.
     * 
     * @param format
     * @param arg
     */
    public abstract void warn(String format, Object arg);

    /**
     * Logs a message at the {@link Level#WARN warn} level.
     * 
     * @param format
     * @param args
     */
    public abstract void warn(String format, Object... args);

    /**
     * Logs a message at the {@link Level#WARN warn} level.
     * 
     * @param message
     * @param t
     */
    public abstract void warn(String message, Throwable t);

    /**
     * Logs a message at the {@link Level#ERROR error} level.
     * 
     * @param message
     */
    public abstract void error(String message);

    /**
     * Logs a message at the {@link Level#ERROR error} level.
     * 
     * @param format
     * @param arg
     */
    public abstract void error(String format, Object arg);

    /**
     * Logs a message at the {@link Level#ERROR error} level.
     * 
     * @param format
     * @param args
     */
    public abstract void error(String format, Object... args);

    /**
     * Logs a message at the {@link Level#ERROR error} level.
     * 
     * @param message
     * @param t
     */
    public abstract void error(String message, Throwable t);

    /**
     * Logs a message at the {@link Level#FATAL fatal} level.
     * 
     * @param message
     */
    public abstract void fatal(String message);

    /**
     * Logs a message at the {@link Level#FATAL fatal} level.
     * 
     * @param format
     * @param arg
     */
    public abstract void fatal(String format, Object arg);

    /**
     * Logs a message at the {@link Level#FATAL fatal} level.
     * 
     * @param format
     * @param args
     */
    public abstract void fatal(String format, Object... args);

    /**
     * Logs a message at the {@link Level#FATAL fatal} level.
     * 
     * @param message
     * @param t
     */
    public abstract void fatal(String message, Throwable t);

    /**
     * Logs a message at the specified {@link Level level}.
     * 
     * @param level
     * @param message
     */
    public abstract void log(Level level, String message);

    /**
     * Logs a message at the specified {@link Level level}.
     * 
     * @param level
     * @param format
     * @param arg
     */
    public abstract void log(Level level, String format, Object arg);

    /**
     * Logs a message at the specified {@link Level level}.
     * 
     * @param level
     * @param format
     * @param args
     */
    public abstract void log(Level level, String format, Object... args);

    /**
     * Logs a message at the specified {@link Level level}.
     * 
     * @param level
     * @param message
     * @param t
     */
    public abstract void log(Level level, String message, Throwable t);
    
    /**
     * Determines if this {@link Logger} is the {@link Logger root logger}.
     */
    public abstract boolean isRoot();

    /**
     * @return The name of this {@link Logger}.
     */
    public String getName();

    /**
     * @return The class targeted by this {@link Logger}.
     */
    public abstract Class<?> getLoggedClass();

    /**
     * @return The priority {@link Level} of this {@link Logger}.
     */
    public abstract Level getLevel();

    /**
     * Sets the priority {@link Level} of this {@link Logger}.
     */
    public abstract Logger setLevel(Level level);

    /**
     * @return The {@link Logger children} of this {@link Logger}.
     */
    public abstract <T extends Logger> List<T> getChildren();

    @Override
    public abstract boolean equals(Object obj);
    
    public record Context(Level level, String message, Object[] args, Throwable t) { }
}
