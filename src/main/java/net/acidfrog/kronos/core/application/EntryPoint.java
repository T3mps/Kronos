package net.acidfrog.kronos.core.application;

import net.acidfrog.kronos.core.lang.logger.Logger;

public class EntryPoint {

	private static long instanceID = 0;
	private static boolean initialized = false;

	private EntryPoint() { }

    public static void initilize() {
		if (!initialized) {
			instanceID = System.currentTimeMillis();
			Logger.instance.initialize();
			initialized = true;
		} else Logger.instance.logError("Application only has one entry point.");
    }

	public static void close() {
		close(0);
	}

    public static void close(int n) {
		Logger.instance.logInfo("Application " + instanceID + " closed");
		Logger.instance.close(n);
    }

}
