package com.starworks.kronos.logging;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.StampedLock;
import java.util.regex.Matcher;

import com.starworks.kronos.logging.appender.Appender;

public final class ConcurrentLogger extends AbstractLogger {

	private final StampedLock m_lock;

	public ConcurrentLogger(String name, Class<?> type, Level level, Layout layout, List<Appender> appenders, boolean ansiFormatting) {
		super(name, type, level, layout, appenders, ansiFormatting);
		this.m_layout = layout;
		this.m_lock = new StampedLock();
	}

	@Override
	public Context log(Level level, String message) {
		long stamp = m_lock.writeLock();
		try {
			if (m_enabled && m_level.allows(level)) {
				Context ctx = createContext(level, message, null);
				m_queue.put(ctx);
				return ctx;
			}
		} catch (InterruptedException ignored) {
		} finally {
			m_lock.unlockWrite(stamp);
		}
		return null;
	}

	@Override
	public Context log(Level level, String message, Throwable t) {
		long stamp = m_lock.writeLock();
		try {
			if (m_enabled && m_level.allows(level)) {
				Context ctx = createContext(level, message, t);
				m_queue.put(ctx);
				return ctx;
			}
		} catch (InterruptedException ignored) {
		} finally {
			m_lock.unlockWrite(stamp);
		}
		return null;
	}

	@Override
	public Context log(Level level, String message, Throwable t, Object... args) {
		if (!m_enabled || !m_level.allows(level)) {
			return null;
		}
		Matcher matcher = s_parameterizedMessagePattern.matcher(message);
		StringBuilder sb = new StringBuilder();
		while (matcher.find()) {
			int index = Integer.parseInt(matcher.group(1));
			try {
				matcher.appendReplacement(sb, String.format("%s", args[index]));
			} catch (ArrayIndexOutOfBoundsException ignored) {
				sb = new StringBuilder();
				sb.append("Passed {");
				sb.append(index);
				sb.append("} parameter to \"");
				sb.append(message);
				sb.append("\" but only provided ");
				sb.append(args.length);
				sb.append(" args: ");
				sb.append(Arrays.toString(args));
				return log(level, sb.toString());
			}
		}
		matcher.appendTail(sb);

		String formattedMessage = sb.toString();
		long stamp = m_lock.writeLock();
		try {
			Context ctx = createContext(level, formattedMessage, t);
			m_queue.put(ctx);
			return ctx;
		} catch (InterruptedException ignored) {
		} finally {
			m_lock.unlockWrite(stamp);
		}

		return null;
	}

	@Override
	public Context log(Context ctx) {
		long stamp = m_lock.writeLock();
		try {
			if (m_enabled && m_level.allows(ctx.level())) {
				m_queue.put(ctx);
			}
		} catch (InterruptedException ignored) {
		} finally {
			m_lock.unlockWrite(stamp);
		}
		return ctx;
	}
}
