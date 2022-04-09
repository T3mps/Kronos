package net.acidfrog.kronos.core;

import net.acidfrog.kronos.core.lang.Std;

/**
 * This class holds the version information for the Kronos library. The
 * version number is defined in three parts: major, minor, and revision.
 * The build is the release name the corresponds to the version number.
 * The release refers to the stability of the build.
 * 
 * <p>
 * <b>Release identifiers: </b>
 * <ul>
 *      <li>'a' = <i>alpha</i>, early releases</li>
 *      <li>'b' = <i>beta</i>, stable early releases</li>
 *      <li>'r' = <i>release</i>, stable release</li>
 *      <li>'n' = <i>nightly</i>, unstable release</li>
 *      <li>'d' = <i>dev</i>, developer build</li>
 * </ul>
 * 
 * @author Ethan Temprovich
 */
public final class Version {

    /** Holds the version numbers, 3 parts. */
    private static final byte[] VERSION = { 0x00, 0x00, 0x03 };

    /** The build name. */
    private static final String BUILD = "neon dynasty";

    /** Holds the release information. */
    private static final char[] RELEASE = { 'd' };

    /**
     * Hidden constructor.
     */
    private Version() {}
    
    /**
     * @return the entire version string.
     */
    public static String get() {
        return major() + "." + minor() + "." + revision() + (RELEASE.length > 1 ? " " : "") + Std.Strings.join(RELEASE, " | ") + " " + build();
    }

    /**
     * @return the major version number[0].
     */
    public static int major() {
        return VERSION[0];
    }

    /**
     * @return the minor version number[1].
     */
    public static int minor() {
        return VERSION[1];
    }

    /**
     * @return the revision version number[2].
     */
    public static int revision() {
        return VERSION[2];
    }

    /**
     * @return the build name.
     */
    public static String build() {
        return BUILD;
    }

    /**
     * @return the release information.
     */
    public static char[] release() {
        return RELEASE;
    }

}
