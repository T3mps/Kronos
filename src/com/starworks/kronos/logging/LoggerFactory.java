package com.starworks.kronos.logging;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.starworks.kronos.Configuration;
import com.starworks.kronos.logging.appender.Appender;
import com.starworks.kronos.logging.appender.ConsoleAppender;
import com.starworks.kronos.logging.appender.RotatingDateFileAppender;

final class LoggerFactory {

	private static final String IMPL 						= 	Configuration.logging.implementation();
	private static final String NAME 						= 	Configuration.logging.name();
	private static final Level LEVEL 						= 	Configuration.logging.level();
	private static final Layout LAYOUT 						= 	Configuration.logging.layout();
	private static final String DIRECTORY 					= 	Configuration.logging.directory();
	private static final int MAX_ROTATING_FILE_LINE_COUNT	=	Configuration.logging.maxRotatingFileLineCount();
	private static final boolean LOG_TO_CONSOLE 			= 	Configuration.logging.logToConsole();
	private static final boolean LOG_TO_FILE 				= 	Configuration.logging.logToFile();
	private static final boolean ANSI_FORMATTING 			= 	Configuration.logging.ansiFormatting();
	
	private static final List<Appender> s_reusableAppenderList = new ArrayList<Appender>(2);
	
	private LoggerFactory() {}

	static Logger create(String name, Class<?> type, Level level, Layout layout, List<Appender> appenders, boolean ansiFormatting) {
		return createFromImplementation(name, type, level, layout, compileAppenders(appenders.toArray(new Appender[appenders.size()])), ansiFormatting);
	}

	static Logger create(String name, Class<?> type, Level level, Appender... appenders) {
		return createFromImplementation(name, type, level, LAYOUT, compileAppenders(appenders), ANSI_FORMATTING);
	}

	static Logger create(String name, Class<?> type, Appender... appenders) {
		return createFromImplementation(name, type, LEVEL, LAYOUT, compileAppenders(appenders), ANSI_FORMATTING);
	}

	static Logger create(Class<?> type, Appender... appenders) {
		return createFromImplementation(NAME, type, LEVEL, LAYOUT, compileAppenders(appenders), ANSI_FORMATTING);
	}

	static Logger create(String name, Class<?> type, Level level) {
		return createFromImplementation(name, type, level, LAYOUT, compileAppenders(), ANSI_FORMATTING);
	}
	
	static Logger create(Class<?> type, Level level) {
		return createFromImplementation(NAME, type, level, LAYOUT, compileAppenders(), ANSI_FORMATTING);
	}

	static Logger create(String name, Class<?> type) {
		return createFromImplementation(name, type, LEVEL, LAYOUT, compileAppenders(), ANSI_FORMATTING);
	}

	static Logger create(Class<?> type) {
		return createFromImplementation(NAME, type, LEVEL, LAYOUT, compileAppenders(), ANSI_FORMATTING);
	}

	static Logger create() {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		for (int i = 2; i < stackTraceElements.length; i++) {
			String className = stackTraceElements[i].getClassName();
			Class<?> callingClass = null;
			try {
				callingClass = Class.forName(className);
				if (callingClass != LoggerFactory.class) {
					return createFromImplementation(NAME, callingClass, LEVEL, LAYOUT, compileAppenders(), ANSI_FORMATTING);
				}
			} catch (ClassNotFoundException ignored) {
				System.err.println("Class " + callingClass + " was not found.");
			}
		}
		return null;
	}
	
	static Logger createFromImplementation(String name, Class<?> type, Level level, Layout layout, List<Appender> appenders, boolean ansiFormatting) {
		try {
			Class<?> loggerClass = Class.forName(IMPL);
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
		if (LOG_TO_CONSOLE) s_reusableAppenderList.add(new ConsoleAppender());
		if (LOG_TO_FILE) s_reusableAppenderList.add(new RotatingDateFileAppender(DIRECTORY, MAX_ROTATING_FILE_LINE_COUNT));
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
