package com.starworks.kronos.jobs;

import com.starworks.kronos.Configuration;

public final class Jobs {

	private static final int UPDATES_PER_SECOND = Configuration.jobs.updatesPerSecond();
	private static final int TIMEOUT_SECONDS = Configuration.jobs.timeoutSeconds();
	private static final int SHUTDOWN_TIMEOUT_SECONDS = Configuration.jobs.shutdownTimeoutSeconds();

	public static JobManager newManager() {
		return newManager(UPDATES_PER_SECOND, TIMEOUT_SECONDS, SHUTDOWN_TIMEOUT_SECONDS);
	}

	public static JobManager newManager(int updatesPerSecond) {
		return newManager(updatesPerSecond, TIMEOUT_SECONDS, SHUTDOWN_TIMEOUT_SECONDS);
	}

	public static JobManager newManager(int updatesPerSecond, int timeoutSeconds) {
		return newManager(updatesPerSecond, timeoutSeconds, SHUTDOWN_TIMEOUT_SECONDS);
	}

	public static JobManager newManager(int updatesPerSecond, int timeoutSeconds, int shutdownTimeoutSeconds) {
		return new JobManager(updatesPerSecond, timeoutSeconds, shutdownTimeoutSeconds);
	}
}
