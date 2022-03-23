package net.acidfrog.kronos.core.lang.logger;

/**
 * Interface for loggers.
 * 
 * @author Ethan Temprovich
 */
public interface LoggerI {

    /**
     * Initializes the logger. Must be called before any other methods.
     * 
     * @return {@code true} if successful, {@code false} otherwise.
     */
    public abstract boolean initialize();

    /**
     * Internal method for logging methods to the console, and file if enabled.
     */
    public abstract void log(final LogLevel level, final String message);

    /**
     * Properly closes the logger. Should be called when the program haults.
     * 
     * @param n state of ending event.
     */
    public abstract void close(int n);
    
}
