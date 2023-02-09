package com.starworks.kronos.jobs;

import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import com.starworks.kronos.exception.Exceptions;
import com.starworks.kronos.logging.Logger;

public class JobManager {
	Logger LOGGER = Logger.getLogger("Main", JobManager.class);

	private final JobQueue m_queue;
	private final int m_updatesPerSecond;
	private final int m_timeoutSeconds;
	private final int m_shutdownTimeoutSeconds;
	private final ExecutorService m_executor;
	private final ScheduledExecutorService m_updateExecutor;
	private final ReentrantLock m_lock;

	JobManager(int updatesPerSecond, int timeoutSeconds, int shutdownTimeoutSeconds) {
		this.m_queue = new JobQueue();
		this.m_updatesPerSecond = updatesPerSecond;
		this.m_timeoutSeconds = timeoutSeconds;
		this.m_shutdownTimeoutSeconds = shutdownTimeoutSeconds;
		var threadFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				JobThread jobThread = new JobThread(r);
				LOGGER.debug("Scheduler thread {0} initialized", jobThread.getName());
				return new JobThread(r);
			}
		};
		this.m_executor = Executors.newCachedThreadPool(threadFactory);
		this.m_updateExecutor = Executors.newSingleThreadScheduledExecutor(threadFactory);
		this.m_lock = new ReentrantLock();
	}

	public void submit(Job<?> job) {
		m_queue.offer(job);
	}

	public void start() {
		LOGGER.info("Job manager started");
		m_updateExecutor.scheduleAtFixedRate(() -> {
			while (!m_updateExecutor.isShutdown()) {
				m_lock.lock();
				try {
					Job<?> job = null;
					try {
						job = m_queue.remove();
					} catch (CompletionException e) {
						Thread.yield();
						continue;
					}
					Future<?> future = m_executor.submit(job);
					try {
						future.get(m_timeoutSeconds, TimeUnit.SECONDS);
					} catch (TimeoutException e) {
						future.cancel(true);
						LOGGER.warn("Job runtime exceeded the allotted timeout duration.", e);
					} catch (InterruptedException ignored) {
					} catch (ExecutionException e) {
						LOGGER.warn("An unhandled exception was thrown during job execution.", e);
					}
				} finally {
					m_lock.unlock();
				}
			}
		}, 0, 1000 / m_updatesPerSecond, TimeUnit.MILLISECONDS);
	}

	public void shutdown() {
		m_updateExecutor.shutdown();
		m_executor.shutdown();
		try {
			if (!m_updateExecutor.awaitTermination(m_shutdownTimeoutSeconds, TimeUnit.SECONDS)) {
				m_updateExecutor.shutdownNow();
			}
			if (!m_executor.awaitTermination(m_shutdownTimeoutSeconds, TimeUnit.SECONDS)) {
				m_executor.shutdownNow();
			}
		} catch (InterruptedException e) {
			LOGGER.warn(Exceptions.getMessage("jobs.jobManager.shutdownTimeout"));
		}
	}

	private static final class JobThread extends Thread {

		private static final AtomicInteger s_counter = new AtomicInteger(0);

		public JobThread(Runnable runnable) {
			super(runnable, "JobThread" + s_counter.getAndIncrement());
		}
	}
}
