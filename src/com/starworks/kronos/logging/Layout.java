package com.starworks.kronos.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Layout {

	private static final Layout MINIMAL = new Layout("[$timestamp<HH:mm:ss>]\n$level: $message");
	
	private static final Layout BASIC   = new Layout("[$timestamp<HH:mm:ss.SSS>] [$name/$level] [$type]: $message");
	
	private static final Layout FULL    = new Layout("[$timestamp<HH:mm:ss.SSS>] [$thread] [$name/$level] [$type::$method@$line]: $message");
	
	private static final Layout JAVA 	= new Layout("$timestamp<yyyy/MM/dd, H:mm:ss a> $type $method\n"
												   + "$level: $message");
	
	private static final Layout LOG4J 	= new Layout("$timestamp<MM-dd-yyyy HH:mm:ss> $type $level [$name] $method $line - $message");
	
	private static final Layout LOGBACK = new Layout("$timestamp<HH:mm:ss.SSS> [$thread] $level in $type[$name] - $message");
	
	private static final Layout CEF 	= new Layout("$timestamp<HH:mm:ss.SSS>|$type::$method@$line|$name|$level|$message");
	
	private static final Layout GELF 	= new Layout("{\n\n"
												   + "\"timestamp\":\"$timestamp<HH:mm:ss.SSS>\"\n\n"
												   + "\"thread\":\"$thread\"\n\n"
												   + "\"name\":\"$name\"\n\n"
												   + "\"type\":\"$type\"\n\n"
												   + "\"message\":\"$message\"\n\n"
												   + "\"level\":\"$level\"\n\n"
												   + "}");
	
	private static final Layout SYSLOG	= new Layout("<$level>$timestamp<MMM dd HH:mm:ss> $name $thread - $message");
	
	private static final Layout JSON 	= new Layout("{\n"
									               + "\t\"timestamp\":\"$timestamp<yyyy-MM-dd'T'HH:mm:ss.SSS>\",\n"
									               + "\t\"name\":\"$name\",\n"
									               + "\t\"level\":\"$level\",\n"
									               + "\t\"type\":\"$type\",\n"
									               + "\t\"message\":\"$message\",\n"
									               + "\t\"line\":\"$line\",\n"
									               + "\t\"method\":\"$method\",\n"
									               + "\t\"thread\":\"$thread\"\n"
									               + "}");
	
	private static final String TIMESTAMP_TOKEN    = "$timestamp";
	private static final String NAME_TOKEN 		   = "$name";
	private static final String LEVEL_TOKEN 	   = "$level";
	private static final String TYPE_TOKEN 		   = "$type";
	private static final String MESSAGE_TOKEN 	   = "$message";
	private static final String LINE_NUMBER_TOKEN  = "$line";
	private static final String METHOD_TOKEN 	   = "$method";
	private static final String THREAD_TOKEN 	   = "$thread";

	private String m_format;
	private DateTimeFormatter m_dateTimeFormatter;

	private Layout(String format) {
		setFormat(format);
	}
	
	Layout(Layout layout) {
		this.m_format = layout.m_format;
		this.m_dateTimeFormatter = layout.m_dateTimeFormatter;
	}

	public static Layout design(String format) {
		return new Layout(format);
	}
	
	public static Layout of(String layout) {
		return switch (layout) {
		case "minimal"  		 		-> minimal();
		case "basic" 			 		-> basic();
		case "full" 			 		-> full();
		case "java" 			 		-> java();
		case "log4j" 			 		-> log4j();
		case "logback" 			 		-> logback();
		case "commoneventformat" 		-> commonEventFormat();
		case "cef" 				 		-> commonEventFormat();
		case "CEF" 				 		-> commonEventFormat();
		case "graylogextendedlogformat" -> graylogExtendedLogFormat();
		case "gelf" 					-> graylogExtendedLogFormat();
		case "GELF" 					-> graylogExtendedLogFormat();
		case "syslog" 					-> syslog();
		case "json" 					-> json();
		case "JSON" 					-> json();
		default 						-> design(layout);
		};
	}
	
	public static Layout minimal() {
		return new Layout(MINIMAL);
	}
	
	public static Layout basic() {
		return new Layout(BASIC);
	}
	
	public static Layout full() {
		return new Layout(FULL);
	}
	
	public static Layout java() {
		return new Layout(JAVA);
	}
	
	public static Layout log4j() {
		return new Layout(LOG4J);
	}
	
	public static Layout logback() {
		return new Layout(LOGBACK);
	}

	public static Layout commonEventFormat() {
		return new Layout(CEF);
	}
	
	public static Layout graylogExtendedLogFormat() {
		return new Layout(GELF);
	}
	
	public static Layout syslog() {
		return new Layout(SYSLOG);
	}
	
	public static Layout json() {
		return new Layout(JSON);
	}
	
	public String format(LocalDateTime timestamp, String name, Level level, Class<?> type, String message, int line, String method, Thread thread) {
		return String.format(m_format, m_dateTimeFormatter.format(timestamp), name, level.toString(), type.getCanonicalName(), message, line, method, thread.getName());
	}

	void setFormat(String logFormat) {
		Pattern pattern = Pattern.compile("\\$\\w+(<[^>]+>)?");
		Matcher matcher = pattern.matcher(logFormat);
		StringBuilder sb = new StringBuilder();
		String timestampFormat = null;
		int size = logFormat.length();
		for (int i = 0; i < size; i++) {
			char c = logFormat.charAt(i);
			if (c == '$') {
				String token = matcher.find(i) ? matcher.group() : "";
				i += token.length() - 1;
				if (token.startsWith(TIMESTAMP_TOKEN)) {
					timestampFormat = token.substring(TIMESTAMP_TOKEN.length() + 1, token.length() - 1);
					sb.append("%1$s");
					continue;
				}
				sb.append(switch (token) {
					case NAME_TOKEN 	   -> "%2$s";
					case LEVEL_TOKEN 	   -> "%3$s";
					case TYPE_TOKEN 	   -> "%4$s";
					case MESSAGE_TOKEN     -> "%5$s";
					case LINE_NUMBER_TOKEN -> "%6$d";
					case METHOD_TOKEN 	   -> "%7$s";
					case THREAD_TOKEN 	   -> "%8$s";
					default				   -> "???";
				});
				continue;
			}

			sb.append(c);
		}

		if (timestampFormat == null) {
			throw new NullPointerException();
		}

		this.m_format = sb.toString();
		this.m_dateTimeFormatter = DateTimeFormatter.ofPattern(timestampFormat);
	}
	
	@Override
	public String toString() {
		return m_format;
	}
}
