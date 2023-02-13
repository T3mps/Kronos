package com.starworks.kronos.logging;

import java.time.LocalDateTime;

import com.starworks.kronos.logging.appender.Appender;

public interface Logger extends AutoCloseable {

	Context log(Level level, String message);

	Context log(Level level, String message, Throwable t);

	default Context log(Level level, String message, Object... args) {
		return log(level, message, null, args);
	}

	Context log(Level level, String message, Throwable t, Object... args);

	Context log(Context ctx);

	default Context trace(String message) {
		return log(Level.TRACE, message);
	}

	default Context trace(String message, Throwable t) {
		return log(Level.TRACE, message, t);
	}

	default Context trace(String message, Object... args) {
		return log(Level.TRACE, message, args);
	}

	default Context trace(String message, Throwable t, Object... args) {
		return log(Level.TRACE, message, t, args);
	}

	default Context debug(String message) {
		return log(Level.DEBUG, message);
	}

	default Context debug(String message, Throwable t) {
		return log(Level.DEBUG, message, t);
	}

	default Context debug(String message, Object... args) {
		return log(Level.DEBUG, message, args);
	}

	default Context debug(String message, Throwable t, Object... args) {
		return log(Level.DEBUG, message, t, args);
	}

	default Context info(String message) {
		return log(Level.INFO, message);
	}

	default Context info(String message, Throwable t) {
		return log(Level.INFO, message, t);
	}

	default Context info(String message, Object... args) {
		return log(Level.INFO, message, args);
	}

	default Context info(String message, Throwable t, Object... args) {
		return log(Level.INFO, message, t, args);
	}

	default Context warn(String message) {
		return log(Level.WARN, message);
	}

	default Context warn(String message, Throwable t) {
		return log(Level.WARN, message, t);
	}

	default Context warn(String message, Object... args) {
		return log(Level.WARN, message, args);
	}

	default Context warn(String message, Throwable t, Object... args) {
		return log(Level.WARN, message, t, args);
	}

	default Context error(String message) {
		return log(Level.ERROR, message);
	}

	default Context error(String message, Throwable t) {
		return log(Level.ERROR, message, t);
	}

	default Context error(String message, Object... args) {
		return log(Level.ERROR, message, args);
	}

	default Context error(String message, Throwable t, Object... args) {
		return log(Level.ERROR, message, t, args);
	}

	default Context fatal(String message) {
		return log(Level.FATAL, message);
	}

	default Context fatal(String message, Throwable t) {
		return log(Level.FATAL, message, t);
	}

	default Context fatal(String message, Object... args) {
		return log(Level.FATAL, message, args);
	}

	default Context fatal(String message, Throwable t, Object... args) {
		return log(Level.FATAL, message, t, args);
	}

	default Context all(String message) {
		return log(Level.ALL, message);
	}

	default Context all(String message, Throwable t) {
		return log(Level.ALL, message, t);
	}

	default Context all(String message, Object... args) {
		return log(Level.ALL, message, args);
	}

	default Context all(String message, Throwable t, Object... args) {
		return log(Level.ALL, message, t, args);
	}

	String getName();

	Class<?> getType();

	Level getLevel();

	Logger setLevel(Level level);

	Layout getLayout();

	Logger setLayout(String formatString);

	Logger setLayout(Layout layout);

	Logger setAnsiFormatting(boolean flag);
	
	boolean isEnabled();

	default void enable() {
		setEnabled(true);
	}

	default void disable() {
		setEnabled(false);
	}
	
	void setEnabled(boolean flag);

	void shutdown();
	
	void close();

	public record Context(LocalDateTime timestamp, String name, Level level, String message, Throwable throwable, int line, String method, Thread thread) {
		static final Context POISION_PILL = new Context(null, "", Level.OFF, "", null, -1, "", null);
	}

	public static Logger getLogger(String name, Class<?> type, Level level, Appender... appenders) {
		return LoggerFactory.create(name, type, level, appenders);
	}

	public static Logger getLogger(String name, Class<?> type, Appender... appenders) {
		return LoggerFactory.create(name, type, appenders);
	}

	public static Logger getLogger(Class<?> type, Appender... appenders) {
		return LoggerFactory.create(type, appenders);
	}

	public static Logger getLogger(String name, Class<?> type, Level level) {
		return LoggerFactory.create(name, type, level);
	}
	
	public static Logger getLogger(Class<?> type, Level level) {
		return LoggerFactory.create(type, level);
	}

	public static Logger getLogger(String name, Class<?> type) {
		return LoggerFactory.create(name, type);
	}

	public static Logger getLogger(Class<?> type) {
		return LoggerFactory.create(type);
	}
	
	public static Logger getLogger() {
		return LoggerFactory.create();
	}
}
