package com.starworks.kronos;

/**
 * This class represents the version of the library. The version is represented
 * by three numbers: major, minor, and revision. The major version number is
 * incremented when there are major changes to the library. The minor version
 * number is incremented when there are minor changes to the library. The
 * revision number is incremented when there are bug fixes or minor changes to
 * the library.
 * 
 * The version number is represented by a single integer. The major version
 * number is stored in the most significant byte, the minor version number is
 * stored in the second most significant byte, and the revision number is stored
 * in the least significant two bytes.
 * 
 * The version number is used to determine if the library is compatible with the
 * version of the library that created the data.
 * 
 * @author Ethan Temprovich
 */
public final class Version {

	// The major version number
	private static final int MAJOR = 0;

	// The minor version number
	private static final int MINOR = 1;

	// The revision version number
	private static final int REVISION = 0;

	private Version() {}

	// Returns true if the version is compatible with the given version
	public static boolean isCompatible(int version) {
		// the major version must be the same
		if (MAJOR != ((version >> 24) & 0xFF)) return false;
		// the minor version must be the same or less
		if (MINOR > ((version >> 16) & 0xFF)) return false;
		// the revision version must be the same or less
		if (REVISION > (version & 0xFFFF)) return false;
		// otherwise they are compatible
		return true;
	}
	
	public static void main(String[] args) {
	}

	// Returns the major version number
	public static int getMajor() {
		return MAJOR;
	}

	// Returns the minor version number
	public static int getMinor() {
		return MINOR;
	}

	// Returns the revision version number
	public static int getRevision() {
		return REVISION;
	}

	// Returns the full version number
	public static int getVersion() {
		return (MAJOR << 24) | (MINOR << 16) | REVISION;
	}

	// Returns the version components as an array
	public static int[] getVersion(int version) {
		return new int[] { (version >> 24) & 0xFF, (version >> 16) & 0xFF, version & 0xFFFF };
	}

	// Returns the version number as a string
	public static String getVersionString() {
		return MAJOR + "." + MINOR + "." + REVISION;
	}

	// Returns the version number as a string
	public static String getVersionString(int version) {
		int major = (version >> 24) & 0xFF;
		int minor = (version >> 16) & 0xFF;
		int revision = version & 0xFFFF;
		return major + "." + minor + "." + revision;
	}
}
