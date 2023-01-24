package com.starworks.kronos.logging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.starworks.kronos.exception.Exceptions;
import com.starworks.kronos.exception.KronosRuntimeException;
import com.starworks.kronos.logging.appender.Appender;
import com.starworks.kronos.toolkit.Builder;

public final class LoggerBuilder implements Builder<Logger> {

	private String m_name;
	private Class<?> m_type;
	private Level m_level;
	private List<Appender> m_appenders;
	private Layout m_layout;
	private boolean m_ansiFormatting;

	public LoggerBuilder() {
		this(null);
	}
	
	public LoggerBuilder(final Class<?> type) {
		this.m_name = Logging.DEFAULT_NAME;
		this.m_type = type;
		this.m_level = Logging.DEFAULT_LEVEL;
		this.m_appenders = new ArrayList<Appender>();
		this.m_layout = Logging.DEFAULT_LAYOUT;
		this.m_ansiFormatting = Logging.DEFAULT_ANSI_FLAG;
	}

	@Override
	public Logger build() {
		if (m_type == null) {
			throw new KronosRuntimeException(Exceptions.getMessage("logging.loggerBuilder.nullType"));
		}
		return LoggerFactory.create(m_name, m_type, m_level, m_layout, m_appenders, m_ansiFormatting);
	}

	public LoggerBuilder setName(final String name) {
		m_name = name;
		return this;
	}

	public LoggerBuilder setType(final Class<?> type) {
		m_type = type;
		return this;
	}

	public LoggerBuilder setLevel(final Level level) {
		m_level = level;
		return this;
	}

	public <A extends Appender> LoggerBuilder addAppender(final A appender) {
		m_appenders.add(appender);
		return this;
	}
	
	public LoggerBuilder addAllAppenders(final Collection<Appender> appenders) {
		m_appenders.addAll(appenders);
		return this;
	}
	
	public LoggerBuilder setAppenders(final List<Appender> appenders) {
		m_appenders = appenders;
		return this;
	}
	
	public LoggerBuilder setLayout(final Layout layout) {
		m_layout = new Layout(layout);
		return this;
	}
	
	public LoggerBuilder setLayout(final String layout) {
		m_layout = Layout.of(layout);
		return this;
	}

	public LoggerBuilder setAnsiFormatting(final boolean ansiFormatting) {
		m_ansiFormatting = ansiFormatting;
		return this;
	}
}
