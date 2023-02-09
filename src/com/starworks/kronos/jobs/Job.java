package com.starworks.kronos.jobs;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;

import com.starworks.kronos.exception.Exceptions;
import com.starworks.kronos.logging.Logger;

public final class Job<T> implements Runnable {
	private final Logger LOGGER = Logger.getLogger(Job.class);

	private final String m_name;
	private final CountDownLatch m_latch;
	private final int m_priority;
	private final Callable<T> m_task;
	private final CompletableFuture<T> m_future;
	private volatile T m_result;
	private final Phaser m_dependencyPhaser;
	private Runnable m_dependencyResolver;
	private final RetryPolicy m_retryPolicy;

	public Job(String name, int priority, Callable<T> callable) {
		this(name, priority, callable, RetryPolicy.basic());
	}

	public Job(String name, int priority, Callable<T> task, RetryPolicy retryPolicy) {
		this.m_name = name;
		this.m_latch = new CountDownLatch(1);
		this.m_priority = priority;
		this.m_task = task;
		this.m_future = new CompletableFuture<T>();
		this.m_result = null;
		this.m_dependencyPhaser = new Phaser(1);
		this.m_dependencyResolver = () -> {};
		this.m_retryPolicy = retryPolicy;
	}

	public Job<T> addDependency(Job<?> job) {
		if (job == null || job == this || job.isCancelled() || job.isComplete()) {
			LOGGER.warn("Attempted to attach an invalid dependency to {0}", m_name);
			return this;
		}
		if (job.getPriority() < m_priority) {
			throw new JobsException(Exceptions.getMessage("jobs.job.matchingDependencyPriority"));
		}
		m_dependencyPhaser.register();
		job.m_dependencyResolver = () -> m_dependencyPhaser.arrive();
		LOGGER.debug("{0} had {1} added as a dependency", m_name, job.m_name);
		return this;
	}

	@Override
	public void run() {
		if (m_latch.getCount() == 0) {
			return;
		}

		int retries = 0;
		long delay = m_retryPolicy.retryDelayUnit().toMillis(m_retryPolicy.retryDelay());
		boolean retry;
		do {
			retry = false;
			try {
				m_dependencyPhaser.arriveAndAwaitAdvance();

				T result = null;
				result = m_task.call();
				m_future.complete(result);
				m_dependencyResolver.run();
				m_result = result;
				break;
			} catch (Throwable t) {
				if (m_latch.getCount() == 0) {
					return;
				}
				if (retries < m_retryPolicy.maxRetries() && m_retryPolicy.condition().test(t)) {
					retry = true;
					retries++;
					try {
						TimeUnit.MILLISECONDS.sleep(delay);
					} catch (InterruptedException e) {
						LOGGER.warn("Job '{0}' was unable to retry", m_name);
						Thread.currentThread().interrupt();
					}
					if (m_retryPolicy.isExponentialBackoff()) {
						delay *= (1 << retries);
					}
				} else {
					m_future.completeExceptionally(t);
					m_dependencyResolver.run();
				}
			} finally {
				m_latch.countDown();
			}
		} while (retry);
	}

	public <U> Job<T> thenApply(Function<? super T, ? extends U> function) {
		Job<T> job = new Job<T>(m_name + ".thenApply", m_priority, m_task, m_retryPolicy);
		job.m_future.thenApply(function);
		return job;
	}

	public <U> Job<T> thenApplyAsync(Function<? super T, ? extends U> function) {
		Job<T> job = new Job<T>(m_name + ".thenApplyAsync", m_priority, m_task, m_retryPolicy);
		job.m_future.thenApplyAsync(function);
		return job;
	}

	public Job<T> thenAccept(Consumer<? super T> action) {
		Job<T> job = new Job<T>(m_name + ".thenAccept", m_priority, m_task, m_retryPolicy);
		job.m_future.thenAccept(action);
		return job;
	}

	public Job<T> thenAcceptAsync(Consumer<? super T> action) {
		Job<T> job = new Job<T>(m_name + ".thenAcceptAsync", m_priority, m_task, m_retryPolicy);
		job.m_future.thenAcceptAsync(action);
		return job;
	}

	public Job<T> thenRun(Runnable action) {
		Job<T> job = new Job<T>(m_name + ".thenRun", m_priority, m_task, m_retryPolicy);
		job.m_future.thenRun(action);
		return job;
	}

	public Job<T> thenRunAsync(Runnable action) {
		Job<T> job = new Job<T>(m_name + ".thenRunAsync", m_priority, m_task, m_retryPolicy);
		job.m_future.thenRunAsync(action);
		return job;
	}

	public T get() throws InterruptedException, ExecutionException {
		return m_future.get();
	}

	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return m_future.get(timeout, unit);
	}

	public void cancel() {
		m_future.cancel(true);
		m_latch.countDown();
	}

	public boolean isCancelled() {
		return m_future.isCancelled();
	}

	public boolean isComplete() {
		return m_future.isDone();
	}

	public String getName() {
		return m_name;
	}

	public int getPriority() {
		return m_priority;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Job[name=");
		builder.append(m_name);
		builder.append(", priority=");
		builder.append(m_priority);
		builder.append(", result=");
		builder.append(m_result);
		builder.append(", retryPolicy=");
		builder.append(m_retryPolicy);
		builder.append("]");
		return builder.toString();
	}
}
