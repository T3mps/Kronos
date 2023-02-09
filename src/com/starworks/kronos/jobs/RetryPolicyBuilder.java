package com.starworks.kronos.jobs;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import com.starworks.kronos.toolkit.Builder;

public final class RetryPolicyBuilder implements Builder<RetryPolicy> {

    private int m_maxRetries;
	private long m_retryDelay;
	private TimeUnit m_retryDelayUnit;
	private boolean m_exponentialBackoff;
	private Predicate<Throwable> m_retryOn;

    public RetryPolicyBuilder() {
        this.m_maxRetries = 0;
        this.m_retryDelay = 0;
        this.m_retryDelayUnit = TimeUnit.MILLISECONDS;
        this.m_exponentialBackoff = false;
        this.m_retryOn = t -> false;
    }

    @Override
    public RetryPolicy build() {
        return new RetryPolicy(m_maxRetries, m_retryDelay, m_retryDelayUnit, m_exponentialBackoff, m_retryOn);
    }

    public RetryPolicyBuilder setMaxRetries(int maxRetries) {
        this.m_maxRetries = maxRetries;
        return this;
    }

    public RetryPolicyBuilder setRetryDelay(long retryDelay, TimeUnit retryDelayUnit) {
        this.m_retryDelay = retryDelay;
        this.m_retryDelayUnit = retryDelayUnit;
        return this;
    }

    public RetryPolicyBuilder setExponentialBackoff(boolean flag) {
        this.m_exponentialBackoff = flag;
        return this;
    }

    public RetryPolicyBuilder setRetryOn(Predicate<Throwable> retryOn) {
        this.m_retryOn = retryOn;
        return this;
    }
}
