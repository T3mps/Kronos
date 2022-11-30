package com.starworks.kronos.logging;

/**
 * This class listens for log events and triggers the appropriate callback.
 * 
 * @author Ethan Temprovich
 * @version 1.0
 * @since 1.0
 */
public interface LoggerListener {

    /**
     * Triggered when a child {@link Logger} is added to another {@link Logger}.
     * 
     * @param parent
     * @param child
     */
    public abstract void onChildAdded(Logger parent, Logger child);

    /**
     * Triggered when a child {@link Logger} is removed from another {@link Logger}.
     * 
     * @param parent
     * @param child
     */
    public abstract void onChildRemoved(Logger parent, Logger child);

    /**
     * Triggered when a {@link Logger} has its parent changed.
     * 
     * @param oldParent
     * @param newParent
     */
    public abstract void onParentChanged(Logger oldParent, Logger newParent);

    /**
     * Triggered when a {@link Logger} logs an event.
     * 
     * @param level
     * @param message
     */
    public abstract void onLog(Level level, String message);
    
    /**
     * Triggered when a {@link Logger} is closed.
     */
    public abstract void onClose();

}
