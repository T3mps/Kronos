package com.starworks.kronos.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.starworks.kronos.logging.callbacks.ConsoleLogCallback;
import com.starworks.kronos.logging.callbacks.LogCallback;

/**
 * This class provides a simple implementation of the {@link Logger} interface.
 * All handeling of messages are delegated to {@link #log0(Level, String, Object[], Throwable)}
 * <p>
 * <strong>NOTE:</strong> On creation, a {@link ConsoleLogCallback} is added to this {@link Logger}.
 * 
 * @author Ethan Temprovich
 * @version 1.0
 * @since 1.0
 * @see Logger
 * @see LoggerFactory
 * @apiNote This class is the default {@link Logger} used by the {@link LoggerFactory}.
 * @apiNote The output stream used by {@link Logger} is not {@link System#out}, instead a
 *          custom output stream defined by {@link ConsoleLogCallback} is used.
 */
final class SimpleLogger implements Logger {

    /**
     * The name of this {@link Logger}.
     */
    private String name;

    /**
     * The targeted class of this {@link Logger}.
     */
    private Class<?> clazz;

    /**
     * The minimum {@link Level} that this {@link Logger} will log.
     */
    private Level level;

    /**
     * The parent of this {@link Logger}.
     */
    private SimpleLogger parent;

    /**
     * The list of children of this {@link Logger}.
     */
    private final List<Logger> children;

    /**
     * Unmodifiable view of the children of this {@link Logger}.
     */
    private final List<Logger> childrenUnmodifiable;

    /**
     * The list of {@link LogCallback log callbacks} of this {@link Logger}.
     */
    private final List<LogCallback> callbacks;

    /**
     * The list of {@link LoggerListener listeners} of this {@link Logger}.
     */
    private final List<LoggerListener> listeners;

    /**
     * Creates a new {@link SimpleLogger} with the given name, class, and parent.
     * 
     * @param name
     * @param clazz
     * @param parent
     */
    public SimpleLogger(String name, final Class<?> clazz, SimpleLogger parent) {
        this.name = name;
        this.clazz = clazz;
        this.parent = parent;
        this.level = (parent == null) ? LogSettings.DEFAULT_LOGGER_LEVEL : ((parent.getLevel() == null) ? LogSettings.DEFAULT_LOGGER_LEVEL : parent.getLevel());
        this.parent = parent;
        this.children = new ArrayList<Logger>();
        this.childrenUnmodifiable = Collections.unmodifiableList(children);
        this.callbacks = List.of(new ConsoleLogCallback(false));
        this.listeners = new ArrayList<LoggerListener>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Logger addChild(Logger child) {
        if (child == null) {
            throw new IllegalArgumentException("Child may not be null.");
        }

        synchronized (this) {
            children.add(child);
            child.setLevel(level);
            child.setParent(this);

            for (var listener : listeners) {
                listener.onChildAdded(this, child);
            }
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Logger getChild(String name) {
        for (var child : children) {
            if (child.getName().equals(name)) {
                return child;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasChild(Logger child) {
        return children.contains(child);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasChild(String name) {
        for (var child : children) {
            if (child.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Logger removeChild(Logger child) {
        if (child == null) {
            throw new IllegalArgumentException("Child may not be null.");
        }
        if (child.getParent() == null) {
            throw new IllegalArgumentException("Child may not be removed from a logger that does not have it as a child.");
        }
        if (child.getParent() != this) {
            throw new IllegalArgumentException("Child may not be removed from another logger.");
        }

        synchronized (this) {
            children.remove(child);
            child.setParent(null);

            for (var listener : listeners) {
                listener.onChildRemoved(this, child);
            }
        }

        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addCallback(LogCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback may not be null.");
        }

        synchronized (this) {
            callbacks.add(callback);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeCallback(LogCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback may not be null.");
        }

        synchronized (this) {
            return callbacks.remove(callback);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerListener(LoggerListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener may not be null.");
        }

        synchronized (this) {
            listeners.add(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean unregisterListener(LoggerListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener may not be null.");
        }

        synchronized (this) {
            return listeners.remove(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void trace(String message) {
        log0(Level.TRACE, message, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void trace(String format, Object arg) {
        log0(Level.TRACE, format, new Object[] { arg }, null);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void trace(String format, Object... args) {
        log0(Level.TRACE, format, args, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void trace(String message, Throwable t) {
        log0(Level.TRACE, message, null, t);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(String message) {
        log0(Level.DEBUG, message, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(String format, Object arg) {
        log0(Level.DEBUG, format, new Object[] { arg }, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(String format, Object... args) {
        log0(Level.DEBUG, format, args, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(String message, Throwable t) {
        log0(Level.DEBUG, message, null, t);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(String message) {
        log0(Level.INFO, message, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(String format, Object arg) {
        log0(Level.INFO, format, new Object[] { arg }, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(String format, Object... args) {
        log0(Level.INFO, format, args, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(String message, Throwable t) {
        log0(Level.INFO, message, null, t);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(String message) {
        log0(Level.WARN, message, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(String format, Object arg) {
        log0(Level.WARN, format, new Object[] { arg }, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(String format, Object... args) {
        log0(Level.WARN, format, args, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(String message, Throwable t) {
        log0(Level.WARN, message, null, t);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(String message) {
        log0(Level.ERROR, message, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(String format, Object arg) {
        log0(Level.ERROR, format, new Object[] { arg }, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(String format, Object... args) {
        log0(Level.ERROR, format, args, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(String message, Throwable t) {
        log0(Level.ERROR, message, null, t);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fatal(String message) {
        log0(Level.FATAL, message, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fatal(String format, Object arg) {
        log0(Level.FATAL, format, new Object[] { arg }, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fatal(String format, Object... args) {
        log0(Level.FATAL, format, args, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fatal(String message, Throwable t) {
        log0(Level.FATAL, message, null, t);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(Level level, String message) {
        log0(level, message, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(Level level, String format, Object arg) {
        log0(level, format, new Object[] { arg }, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(Level level, String format, Object... args) {
        log0(level, format, args, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(Level level, String message, Throwable t) {
        log0(level, message, null, t);
    }

    /**
     * Chief method for logging.
     * 
     * @param level
     * @param msg
     * @param args
     * @param t
     */
    private void log0(Level level, String msg, Object[] args, Throwable t) {
        if (!level.hasPrecedence(this.level) || msg == null) {
            return;
        }

        synchronized (this) {
            StringBuilder sb = new StringBuilder();

            // append the current time
            sb.append(new SimpleDateFormat(LogSettings.MESSAGE_TIME_FORMAT).format(new Date()));

            sb.append(" [");
            sb.append(name);
            sb.append("] ");

            StringBuilder lvl = new StringBuilder().append(level.toString());
            do {
                lvl.append(" ");
            } while (lvl.length() < 6); // 5 is the length of the longest level name + 1 for the space, should be a variable but eh...
            
            sb.append(lvl.toString());

            sb.append(clazz.getSimpleName());
            sb.append(".java");
            sb.append(" - ");

            if (args == null) {
                sb.append(msg);
            } else {
                int idx = 0;
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Throwable) {
                        sb.append(msg.replace("{" + i + "}", ""));
                        sb.append("\n");
                        ((Throwable) args[i]).printStackTrace(new PrintStream(new OutputStream() {
                            @Override
                            public void write(int b) throws IOException {
                                sb.append((char) b);
                            }
                        }));
                    } else {
                        sb.append(msg.replace("{" + i + "}", args[i].toString()));
                    }
                }

                if (idx != 0) {
                    sb.insert(idx, msg);
                }
            }

            if (t != null) {
                sb.append(" { ");
                sb.append(ANSI.NEWLINE);
                sb.append('\t');
                sb.append(t.getMessage());
                sb.append(ANSI.NEWLINE);
                sb.append("}");
            }

            String msgStr = sb.toString();

            for (var callback : callbacks) {
                callback.log(new Context(level, msgStr, args, t));
            }
            
            for (var listener : listeners) {
                listener.onLog(level, msgStr);
            }
        }
    }

    /**
     * {@inheritDoc}
     * @throws Exception
     */
    @Override
    public void close() {
        synchronized (this) {
            try {
                for (var callback : callbacks) {
                    callback.close();
                }
                callbacks.clear();
                
                for (var listener : listeners) {
                    listener.onClose();
                }
                listeners.clear();
                
                int cSize = children.size();
                for (int i = 0; i < cSize; i++) {
                    children.get(i).close();
                }
                if (parent != null) {
                    parent.removeChild(this);
                }
                
                children.clear();

                LoggerFactory.loggers.remove(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this {@link Logger}.
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getLoggedClass() {
        return clazz;
    }

    /**
     * Sets the class that this {@link Logger} is logging.
     * 
     * @param clazz
     */
    public void setLoggedClass(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Level getLevel() {
        return level;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SimpleLogger setLevel(Level level) {
        if (this.level == level) {
            return this;
        }
        if (level == null && this.isRoot()) {
            throw new IllegalArgumentException("Root level may not be set to null.");
        }
        
        synchronized (this) {
            this.level = level == null ? parent.level : level;

            if (children != null) {
                int cSize = children.size();

                for (int i = 0; i < cSize; i++) {
                    var child = children.get(i);
                    child.setLevel(level);
                }
            }

            return this;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SimpleLogger getParent() {
        return parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Logger setParent(Logger parent) {
        Logger old = this.parent;
        this.parent = (SimpleLogger) parent;
        for (LoggerListener listener : listeners) {
            listener.onParentChanged(old, parent);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Logger> getChildren() {
        return childrenUnmodifiable;
    }
}
