package com.starworks.kronos.logging;

/**
 * {@link LevelNotFoundException} is thrown when a {@link Level} is attempted to be
 * defined, but does not exist.
 * 
 * @author Ethan Temprovich
 * @version 1.0
 * @since 1.0
 */
public class LevelNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@link LevelNotFoundException} with the generic message.
     */
    public LevelNotFoundException() {
        super("The specified Level does not exist.");
    }

    /**
     * Constructs a new {@link LevelNotFoundException} with the specified message.
     * 
     * @param message
     *            The message to be displayed.
     */
    public LevelNotFoundException(String message) {
        super(message);
    }
}
