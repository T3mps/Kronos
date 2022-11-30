package com.starworks.kronos.logging.callbacks;

import java.io.Closeable;

import com.starworks.kronos.logging.Logger.Context;

/**
 * This class defines the callback interface for the {@link Logger} class. This
 * interface is used to define the way messages are handled.
 * 
 * @author Ethan Temprovich
 * @version 1.0
 * @since 1.0
 */
public interface LogCallback extends Closeable {

    /**
     * Called when an event is logged.
     * 
     * @param level
     * @param message
     */
    public abstract void log(Context ctx);

}
