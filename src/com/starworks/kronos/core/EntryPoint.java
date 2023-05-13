package com.starworks.kronos.core;

import javax.management.InstanceAlreadyExistsException;

import com.starworks.kronos.Configuration;
import com.starworks.kronos.Version;
import com.starworks.kronos.logging.Logger;
import com.starworks.kronos.toolkit.Reflections;
import com.starworks.kronos.toolkit.SystemInfo;
import com.starworks.kronos.toolkit.concurrent.ArrivalGate;

public final class EntryPoint {

	/** The target {@link Configuration configuration} file specifies a different version of Kronos. */
	private static final int NON_MATCHING_CONFIGURATION_VERSION = -3;

	/** We only want to load the {@link Configuration configuration} file once. */
	private static final int CONFIGURATION_ALREADY_LOADED = -2;

	/** {@link EntryPoint} failed to initialize. */
	private static final int ENTRY_POINT_ALREADY_INITIALIZED = -1;

	/** Application closed without incident. */
	private static final int NORMAL = 0;

	/** More than one instance of {@link EntryPoint} was instantiated. This should never happen. */
	private static final int MULTIPLE_INSTANTIATION_VIOLATION = 1;

	static {
		try {
			Configuration.load("data/configuration.xml");
		} catch (InstanceAlreadyExistsException e) {
			System.exit(CONFIGURATION_ALREADY_LOADED);
		} catch (IllegalStateException e) {
			System.exit(NON_MATCHING_CONFIGURATION_VERSION);
		}
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
			exit(ENTRY_POINT_ALREADY_INITIALIZED);
		}
		{ // system information
			LOGGER.info("Operating System: {0} {1}", SystemInfo.getOSName(), SystemInfo.getOSArchitecture());
			LOGGER.info("Available Memory: {0} MB", SystemInfo.getMemory());
			LOGGER.info("CPU Cores: {0}", SystemInfo.getCPUCores());
			LOGGER.info("CPU Name: {0}", SystemInfo.getCPUName());
			LOGGER.info("CPU Architecture: {0}", SystemInfo.getCPUArchitecture());

			LOGGER.info("Java Version: {0}", SystemInfo.getJavaVersion());
			LOGGER.info("Java Vendor: {0}", SystemInfo.getJavaVendor());
			LOGGER.info("Java VM: {0} {1}", SystemInfo.getJvmName(), SystemInfo.getJvmVersion());
			LOGGER.info("Java VM Vendor: {0}", SystemInfo.getJvmVendor());
		}
		{ // kronos information
			LOGGER.info("Application Implementation: {0}", Configuration.runtime.implementation());
			LOGGER.info("Runtime Update Interval: {0}/s", Configuration.runtime.updatesPerSecond());
			LOGGER.info("Runtime Fixed Update Rate: {0}/s", Configuration.runtime.fixedUpdatesPerSecond());
			LOGGER.info("Debug Mode: {0}", Configuration.runtime.debug());
			LOGGER.info("Logging Level: {0}", Configuration.logging.level());
		}

		String applicationClass = Configuration.runtime.implementation();
		if (applicationClass == "") {
			// first time run
			LOGGER.warn("\n\n\t###########################  IMPORTANT  ###########################"
					+ "\n\n\tApplication implementation not set. Open working directory"
					+ "\n\t(default ../AppData/Roaming/Kronos) and open configuration.xml."
					+ "\n\tSet the implementation attribute on the application element to the"
					+ "\n\tfully qualified name of your class."
					+ "\n\n\t###################################################################");
			exit(NORMAL);
		}
		try (var application = (Application) Reflections.newInstance(applicationClass)) {
			application.initialize();
			application.start();
		} finally {
			try {
				s_gate.depart();
			} catch (InterruptedException critical) {
				LOGGER.fatal("More than once instance of Kronos' entry point has been instantiated.", critical);
				exit(MULTIPLE_INSTANTIATION_VIOLATION);
			}
			exit(NORMAL);
		}
	}

	private void exit(int status) {
		Thread.yield();
		LOGGER.close();
		System.exit(status);
	}
}
