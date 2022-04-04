package net.acidfrog.kronos.core;

import java.io.PrintStream;
import java.util.Locale;

// import NativeLibrary.java


import net.acidfrog.kronos.core.lang.annotations.Internal;
import net.acidfrog.kronos.core.lang.logger.Logger;
import net.acidfrog.kronos.math.Mathk;
import net.acidfrog.kronos.math.Options;

/**
 * Configuration options for Kronos.
 * 
 * @author Ethan Temprovich
 */
@Internal
public final class Config {

    // system vars

    public static final String OPERATING_SYSTEM = OSArbiter.getOperatingSystemName();
    // private static final String CONFIG_PATH = "data/.config";
    public static final String JAVA_VERSION = System.getProperty("java.version");
    public static final String JAVA_VENDOR = System.getProperty("java.vendor");
    public static final String OPENGL_VERSION = "OpenGL 4.6";

    // core vars

    /** Debug state for Kronos. */
    public static final boolean DEBUG = true;

    /** Release state for Kronos. */
    public static final boolean RELEASE = false;

    public static final String KRONOS_BUILD = (RELEASE ? "Release" : "Dev") + " Build";

    /** How many times a second Kronos updates. */
    public static final float REGULAR_UPDATE_RATE = 60.0f;

    /** Should the {@link Logger} output to file? */
    public static final boolean LOG_TO_FILE = true;

    /** Should the {@link Logger}s console output be colorized? */
    public static final boolean ESCALATION_COLORS = true;

    /** Save path for KRON. */
    public static String KRON_SAVE_PATH = "data/kron/";

    /** Save path for {@link Scene scenes}. */
    public static String SCENE_SAVE_PATH = "data/scenes/";

    /** The @link PrintStream error stream} for the {@link Logger}.*/
    public static final PrintStream ERROR_STREAM = System.err;

    /** {@link Logger} file output save path. */
    public static String LOGGER_SAVE_PATH = "data/logs/";

    // physics vars

    /** {@code 1 / PHYSICS_UPDATE_RATE} = physics timestep. */
    public static final float PHYSICS_UPDATE_RATE = 50.0f;

    /** Gravity y-component. */
    public static final float GRAVITY = 9.81f; // 955.0f;

    // math vars

    /** 
     * Should we use fast of approximations calculations in {@link Mathk}?
     * 
     * @see Mathk
     * @see Options#FASTMATH
     */
    public static final boolean FASTMATH = true;

    /** 
     * Should we use faster, inaccurate approximations of calculations in {@link Mathk}?
     * 
     * @see Mathk
     * @see Options#IMPRECISE_MATH
     */
    public static final boolean IMPRECISE_MATH = false;

    /** 
     * Should we use the sine lookup table?
     * 
     * @see Mathk
     * @see Options#SIN_LOOKUP
     */
    public static final boolean SIN_LOOKUP = true;

    /** 
     * The number of bits to use for sine lookup.
     * 
     * @see Mathk
     * @see Options#SIN_LOOKUP_BITS
     */
    public static final int SIN_LOOKUP_BITS = SIN_LOOKUP ? 14 : 0;

    /**
     * Logs the current configuration state.
     */
    public static void logEntries() {
        Logger.logInfo("Operating system: " + Config.OPERATING_SYSTEM);
		Logger.logInfo("Java version: Java SE " + Config.JAVA_VERSION);
		Logger.logInfo("Java vendor: " + Config.JAVA_VENDOR);
        
        Logger.logInfo("Kronos version: " + Version.get());
        Logger.logInfo("Debug mode: " + Config.DEBUG);
        Logger.logInfo("Release mode: " + Config.RELEASE);
        Logger.logInfo("Regular update rate: " + Config.REGULAR_UPDATE_RATE);
        Logger.logInfo("Physics update rate: " + Config.PHYSICS_UPDATE_RATE);
        Logger.logInfo("Gravity: " + Config.GRAVITY);
        Logger.logInfo("Fast math: " + Config.FASTMATH);
        Logger.logInfo("Imprecise math: " + Config.IMPRECISE_MATH);
        Logger.logInfo("Sin lookup bits: " + Config.SIN_LOOKUP_BITS);
    }

    /**
     * Determines the current operating system.
     * 
     * @author Ethan Temprovich
     */
    public final class OSArbiter {

        /** Name of 'win'. */
        public static final String WINDOWS         =   "Windows";

        /** Name of 'mac'. */
        public static final String MAC             =   "Darwin";

        /** Name of 'nux'. */
        public static final String LINUX           =   "Linux";

        /** Unsuppored OS. */
        public static final String NOT_SUPPORTED   =   "NOT_SUPPORTED";

        /** Cached OS. */
        private static String os;

        private static int arch = -1;
        
        /**
         * Determines the current operating system, and caches it.
         * 
         * @return The current operating system.
         */
        public static String getOperatingSystemName() {
            if (os == null) {
                String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            
                if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) os = MAC;
                else if (OS.indexOf("win") >= 0) os = WINDOWS;
                else if (OS.indexOf("nux") >= 0) os = LINUX;
                else os = NOT_SUPPORTED;
            }

            return os + " " + System.getProperty("os.arch");
        }

        /**
         * Determines the current operating system architecture.
         * 
         * @return The current operating system architecture.
         */
        public static native int getOperatingSystemArchitecture();

    }
}
