package com.starworks.kronos.toolkit.crypto;

public class Hash {

	public static final CRC32 CRC32 = new CRC32();
	public static final DJB2 DJB2 = new DJB2();
	public static final DJB2a DJB2a = new DJB2a();
	public static final MurmurHash2 MURMUR2 = new MurmurHash2();
	public static final MurmurHash3 MURMUR3 = new MurmurHash3();
	public static final CityHash CITYHASH = new CityHash();
	
	private Hash() {
	}
}
