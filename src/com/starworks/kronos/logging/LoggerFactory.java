package com.starworks.kronos.logging;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.starworks.kronos.logging.appender.Appender;
import com.starworks.kronos.logging.appender.ConsoleAppender;
import com.starworks.kronos.logging.appender.RotatingDateFileAppender;
import com.starworks.kronos.toolkit.Factory;

final class LoggerFactory implements Factory<Logger> {

	private static final List<Appender> s_reusableAppenderList = new ArrayList<Appender>(2);
	
	private LoggerFactory() {}

	@Override
	public Logger create() {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		for (int i = 2; i < stackTraceElements.length; i++) {
			String className = stackTraceElements[i].getClassName();
			try {
				Class<?> callingClass = Class.forName(className);
				if (callingClass != LoggerFactory.class) {
					return create(Logging.DEFAULT_NAME, callingClass, Logging.DEFAULT_LEVEL, Logging.DEFAULT_LAYOUT, compileAppenders(), Logging.DEFAULT_ANSI_FLAG);
				}
			} catch (ClassNotFoundException ignored) {
			}
		}
		return null;
	}

	static Logger create(String name, Class<?> type, Level level, Layout layout, List<Appender> appenders, boolean ansiFormatting) {
		return createFromImplementation(name, type, level, layout, compileAppenders(appenders.toArray(new Appender[appenders.size()])), ansiFormatting);
	}

	static Logger create(String name, Class<?> type, Level level, Appender... appenders) {
		return createFromImplementation(name, type, level, Logging.DEFAULT_LAYOUT, compileAppenders(appenders), Logging.DEFAULT_ANSI_FLAG);
	}

	static Logger create(String name, Class<?> type, Appender... appenders) {
		return createFromImplementation(name, type, Logging.DEFAULT_LEVEL, Logging.DEFAULT_LAYOUT, compileAppenders(appenders), Logging.DEFAULT_ANSI_FLAG);
	}

	static Logger create(Class<?> type, Appender... appenders) {
		return createFromImplementation(Logging.DEFAULT_NAME, type, Logging.DEFAULT_LEVEL, Logging.DEFAULT_LAYOUT, compileAppenders(appenders), Logging.DEFAULT_ANSI_FLAG);
	}

	static Logger create(String name, Class<?> type, Level level) {
		return createFromImplementation(name, type, level, Logging.DEFAULT_LAYOUT, compileAppenders(), Logging.DEFAULT_ANSI_FLAG);
	}

	static Logger create(String name, Class<?> type) {
		return createFromImplementation(name, type, Logging.DEFAULT_LEVEL, Logging.DEFAULT_LAYOUT, compileAppenders(), Logging.DEFAULT_ANSI_FLAG);
	}

	static Logger create(Class<?> type) {
		return createFromImplementation(Logging.DEFAULT_NAME, type, Logging.DEFAULT_LEVEL, Logging.DEFAULT_LAYOUT, compileAppenders(), Logging.DEFAULT_ANSI_FLAG);
	}

	static Logger createFromImplementation(String name, Class<?> type, Level level, Layout layout, List<Appender> appenders, boolean ansiFormatting) {
		try {
			Class<?> loggerClass = Class.forName(Logging.LOGGER_IMPLEMENTATION);
			Constructor<?> constructor = loggerClass.getConstructor(String.class, Class.class, Level.class, Layout.class, List.class, boolean.class);
			Logger logger = (Logger) constructor.newInstance(name, type, level, layout, appenders, ansiFormatting);
			return logger;
		} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static List<Appender> compileAppenders(Appender... appenders) {
		s_reusableAppenderList.clear();
		if (Logging.LOG_TO_CONSOLE) s_reusableAppenderList.add(new ConsoleAppender());
		if (Logging.LOG_TO_FILE) s_reusableAppenderList.add(new RotatingDateFileAppender(Logging.DIRECTORY, Logging.MAX_ROTATING_FILE_LINES));
		int size = appenders.length;
		for (int i = 0; i < size; i++) {
			var appender = appenders[i];
			if (!s_reusableAppenderList.contains(appender)) {
				s_reusableAppenderList.add(appender);
			}
		}
		return s_reusableAppenderList;
	}
}
