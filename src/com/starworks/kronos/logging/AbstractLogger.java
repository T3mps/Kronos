package com.starworks.kronos.logging;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.starworks.kronos.logging.appender.Appender;
import com.starworks.kronos.toolkit.Ansi;
import com.starworks.kronos.toolkit.Ansi.AnsiTrait;

public abstract class AbstractLogger implements Logger, Runnable {

	public static final int BUFFER_SIZE = 1 << 10;

	protected static final Pattern s_parameterizedMessagePattern = Pattern.compile("\\{(\\d+)\\}");

	protected final String m_name;
	protected final Class<?> m_type;
	protected Level m_level;
	protected Layout m_layout;
	protected final List<Appender> m_appenders;
	private final Semaphore m_semaphore;
	protected final ExecutorService m_executor;
	protected boolean m_enabled;
	protected final BlockingQueue<Logger.Context> m_queue;
	private boolean m_ansiFormatting;
	private volatile boolean m_shutdown;

	AbstractLogger(String name, Class<?> type, Level level, Layout layout, List<Appender> appenders, boolean ansiFormatting) {
		this.m_name = name;
		this.m_type = type;
		this.m_level = level;
		this.m_layout = layout;
		this.m_appenders = new CopyOnWriteArrayList<Appender>(appenders);
		this.m_semaphore = new Semaphore(1);
		this.m_executor = Executors.newSingleThreadExecutor();
		this.m_enabled = true;
		this.m_queue = new ArrayBlockingQueue<Logger.Context>(BUFFER_SIZE);
		this.m_ansiFormatting = ansiFormatting;
		this.m_shutdown = false;
		m_executor.submit(this);
	}

	public <A extends Appender> AbstractLogger addAppender(A appender) {
		if (!m_appenders.contains(appender)) {
			m_appenders.add(appender);
		}
		return this;
	}

	public <A extends Appender> AbstractLogger removeAppender(A appender) {
		m_appenders.remove(appender);
		return this;
	}

	@Override
	public void run() {
		try {
			for (;;) {
				try {
					m_semaphore.acquire();
					if (m_enabled) {
						process();
					}
				} catch (PoisonPill p) {
					break;
				} finally {
					m_semaphore.release();
				}
			}
		} catch (InterruptedException e) {
			throw new ConcurrentModificationException(e);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			m_shutdown = true;
		}
	}

	private final void process() throws InterruptedException, IOException, PoisonPill {
		Logger.Context ctx = m_queue.take();
		if (ctx.line() == -1) throw new PoisonPill();

		var timestamp = ctx.timestamp();
		var name = ctx.name();
		var level = ctx.level();
		var message = ctx.message();
		var throwable = ctx.throwable();
		var line = ctx.line();
		var method = ctx.method();
		var thread = ctx.thread();

		var formatted = new StringBuilder(m_layout.format(timestamp, name, level, m_type, message, line, method, thread));
		formatted.append(System.lineSeparator());

		write(new Appender.Message(formatted.toString(), m_ansiFormatting ? level.getTraits() : null));

		if (throwable != null) {
			printStackTrace(throwable, formatted);
			write(new Appender.Message(formatted.toString(), m_ansiFormatting ? new AnsiTrait[] { Ansi.Traits.RED_FG } : null));
		}
	}

	protected void write(Appender.Message message) throws IOException {
		for (var appender : m_appenders) {
			appender.append(message);
		}
	}

	private void printStackTrace(Throwable throwable, StringBuilder sb) {
		final String newLine = System.lineSeparator();

		sb.setLength(0);
		var stackTrace = throwable.getStackTrace();
		int stackTraceLength = stackTrace.length;
		for (int i = 0; i < stackTraceLength; i++) {
			sb.append("\tat\s");
			sb.append(stackTrace[i].toString());
			if (i < stackTraceLength - 1) {
				sb.append(newLine);
			}
		}
		sb.append(newLine);
	}

	protected final Logger.Context createContext(Level level, String message, Throwable t) {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		StackTraceElement callingElement = findCallingElement(stackTrace);
		String methodName = callingElement.getMethodName();
		int lineNumber = callingElement.getLineNumber();
		return new Logger.Context(LocalDateTime.now(), m_name, level, message, t, lineNumber, methodName, Thread.currentThread());
	}

	protected final StackTraceElement findCallingElement(StackTraceElement[] stackTrace) {
		int size = stackTrace.length;
		String target = getType().getCanonicalName();
		for (int i = 0; i < size; i++) {
			StackTraceElement element = stackTrace[i];
			if (element.getClassName().equals(target)) {
				return element;
			}
		}
		throw new IllegalStateException("Attempting to log " + target + " outside of class bounds.");
	}

	public void close() {
		shutdown();
		for (var appender : m_appenders) {
			try {
				appender.close();
			} catch (IOException ignored) {
				System.err.println("Failed to close appender " + appender.getClass().getSimpleName());
			}
		}
	}

	@Override
	public final void shutdown() {
		try {
			m_queue.put(Context.POISION_PILL);
			while (!m_shutdown);
			m_executor.shutdown();
			m_executor.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.err.println("Failed to terminate logger single thread executor within 1 second");
			m_executor.shutdownNow();
		}
	}

	@Override
	public String getName() {
		return m_name;
	}

	@Override
	public Class<?> getType() {
		return m_type;
	}

	@Override
	public Level getLevel() {
		return m_level;
	}

	@Override
	public Logger setLevel(Level level) {
		m_level = level;
		return this;
	}

	@Override
	public Layout getLayout() {
		return m_layout;
	}

	@Override
	public Logger setLayout(String layout) {
		m_layout = Layout.of(layout);
		return this;
	}

	@Override
	public Logger setLayout(Layout layout) {
		m_layout = new Layout(layout);
		return this;
	}

	@Override
	public Logger setAnsiFormatting(boolean flag) {
		m_ansiFormatting = flag;
		return this;
	}
	
	@Override
	public List<Appender> getAppenders() {
		return Collections.unmodifiableList(m_appenders);
	}

	@Override
	public boolean isEnabled() {
		return m_enabled;
	}

	@Override
	public void setEnabled(boolean flag) {
		this.m_enabled = flag;
	}

	private class PoisonPill extends Throwable {
		private static final long serialVersionUID = 1L;
	}
}
