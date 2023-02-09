package com.starworks.kronos.jobs;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public final class RetryPolicy {

	private static final RetryPolicy BASIC				 	=	 new RetryPolicy().withMaxRetries(0);
	private static final RetryPolicy LINEAR_BACKOFF 		=	 new RetryPolicy().withMaxRetries(3).withRetryDelay(1000, TimeUnit.MILLISECONDS).retryOn(t -> true);
	private static final RetryPolicy EXPONENTIAL_BACKOFF 	=	 new RetryPolicy().withMaxRetries(3).withRetryDelay(1000, TimeUnit.MILLISECONDS).withExponentialBackoff().retryOn(t -> true);

	private int m_maxRetries;
	private long m_retryDelay;
	private TimeUnit m_retryDelayUnit;
	private boolean m_exponentialBackoff;
	private Predicate<Throwable> m_retryOn;

	private RetryPolicy() {
		this.m_maxRetries = 0;
		this.m_retryDelay = 0;
		this.m_retryDelayUnit = TimeUnit.MILLISECONDS;
		this.m_exponentialBackoff = false;
		this.m_retryOn = t -> false;
	}

	RetryPolicy(int maxRetries, long retryDelay, TimeUnit retryDelayUnit, boolean exponentialBackoff, Predicate<Throwable> retryOn) {
		this.m_maxRetries = maxRetries;
		this.m_retryDelay = retryDelay;
		this.m_retryDelayUnit = retryDelayUnit;
		this.m_exponentialBackoff = exponentialBackoff;
		this.m_retryOn = retryOn;
	}

	private RetryPolicy(RetryPolicy retryPolicy) {
		this(retryPolicy.m_maxRetries, retryPolicy.m_retryDelay, retryPolicy.m_retryDelayUnit, retryPolicy.m_exponentialBackoff, retryPolicy.m_retryOn);
	}

	public static RetryPolicy basic() {
		return new RetryPolicy(BASIC);
	}

	public static RetryPolicy linearBackoff() {
		return new RetryPolicy(LINEAR_BACKOFF);
	}

	public static RetryPolicy exponentialBackoff() {
		return new RetryPolicy(EXPONENTIAL_BACKOFF);
	}

	public RetryPolicy withMaxRetries(int maxRetries) {
		this.m_maxRetries = maxRetries;
		return this;
	}

	public RetryPolicy withRetryDelay(long retryDelay, TimeUnit retryDelayUnit) {
		this.m_retryDelay = retryDelay;
		this.m_retryDelayUnit = retryDelayUnit;
		return this;
	}

	public RetryPolicy withExponentialBackoff() {
		this.m_exponentialBackoff = true;
		return this;
	}

	public RetryPolicy retryOn(Predicate<Throwable> retryOn) {
		this.m_retryOn = retryOn;
		return this;
	}

	public int maxRetries() {
		return m_maxRetries;
	}

	public long retryDelay() {
		return m_retryDelay;
	}

	public TimeUnit retryDelayUnit() {
		return m_retryDelayUnit;
	}

	public boolean isExponentialBackoff() {
		return m_exponentialBackoff;
	}

	Predicate<Throwable> condition() {
		return m_retryOn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RetryPolicy [maxRetries(");
		builder.append(m_maxRetries);
		builder.append(") | retryDelay(");
		builder.append(m_retryDelay);
		builder.append('\s');
		builder.append(m_retryDelayUnit.toChronoUnit());
		builder.append(") | exponentialBackoff(");
		builder.append(m_exponentialBackoff);
		builder.append(") | retryOn(");
		builder.append(m_retryOn.toString().replace("$$Lambda$", "Predicate$"));
		builder.append(']');
		return builder.toString();
	}
}
