package com.starworks.kronos.core;

import static com.starworks.kronos.core.ErrorCode.CONCURRENT_ACCESS_VIOLATION;
import static com.starworks.kronos.core.ErrorCode.FAILED_TO_INITIALIZE;
import static com.starworks.kronos.core.ErrorCode.NORMAL;

import com.starworks.kronos.Configuration;
import com.starworks.kronos.Kronos;
import com.starworks.kronos.Version;
import com.starworks.kronos.files.FileSystem;
import com.starworks.kronos.logging.Logger;
import com.starworks.kronos.toolkit.Reflections;
import com.starworks.kronos.toolkit.SystemInfo;
import com.starworks.kronos.toolkit.concurrent.ArrivalGate;

public final class EntryPoint {

	static {
		Kronos.validateMapXSD();
	}

	private final Logger LOGGER = Logger.getLogger(EntryPoint.class);

	private static final ArrivalGate s_gate = new ArrivalGate(1);

	public EntryPoint() {
		LOGGER.info("Kronos Engine v{0}", Version.getVersionString());
		LOGGER.info("Developed by Starworks");

		try {
			s_gate.arrive();
		} catch (InterruptedException critical) {
			LOGGER.fatal("Kronos failed to initialize.", critical);
			exit(FAILED_TO_INITIALIZE);
		}
		LOGGER.info("Operating System: {0} {1}", SystemInfo.getOSName(), SystemInfo.getOSArchitecture());
		LOGGER.info("Available Memory: {0} MB", SystemInfo.getMemory());
		LOGGER.info("CPU Cores: {0}", SystemInfo.getCPUCores());
		LOGGER.info("CPU Name: {0}", SystemInfo.getCPUName());
		LOGGER.info("CPU Architecture: {0}", SystemInfo.getCPUArchitecture());

		LOGGER.info("Java Version: {0}", SystemInfo.getJavaVersion());
		LOGGER.info("Java Vendor: {0}", SystemInfo.getJavaVendor());
		LOGGER.info("Java VM: {0} {1}", SystemInfo.getJvmName(), SystemInfo.getJvmVersion());
		LOGGER.info("Java VM Vendor: {0}", SystemInfo.getJvmVendor());

		String applicationClass = Configuration.APPLICATION_IMPL;
		try (var application = (Application) Reflections.newInstance(applicationClass)) {
			application.initialize();
			Kronos.preinclude();
			application.start();
		} finally {
			try {
				s_gate.depart();
			} catch (InterruptedException critical) {
				LOGGER.fatal("More than once instance of Kronos' entry point has been instantiated.", critical);
				exit(CONCURRENT_ACCESS_VIOLATION);
			}
			exit(NORMAL);
		}
	}

	private void exit(int status) {
		Thread.yield();
		LOGGER.close();
		if (status >= 0) GLFWContext.terminate();
		System.exit(status);
	}
}
