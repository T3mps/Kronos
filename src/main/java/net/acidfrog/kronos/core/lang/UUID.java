package net.acidfrog.kronos.core.lang;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.acidfrog.kronos.core.lang.assertions.Asserts;
import net.acidfrog.kronos.mathk.random.MT19937;

/**
 * This class stores two {@link Long 64 bit integers} that represent a greater
 * 128 bit value. This value is comprised of the first 8 bytes of a random 16
 * {@link Byte byte} (128 bit) array, which are defined as the 
 * {@link #mostSigBits most significant bits}, and the last 8 bytes of the array,
 * the {@link #leastSigBits least significant bits}.
 * 
 * <p>
 * The version defines a value that describes how this {@code UUID} was generated.
 * There are four different basic types of UUIDs: time-based, DCE
 * security, name-based, and randomly generated UUIDs.  These types have a
 * version value of 1, 2, 3 and 4, respectively.
 * 
 * <p>
 * Instances of
 * {@code net.acidfrog.kronos.core.lang.UUID} are threadsafe. This is because
 * the {@link MT19937 PRNG engine} used in this class is also threadsafe.
 * 
 * @author Ethan Temprovich
 */
public final class UUID implements Serializable, Comparable<UUID> {
    private static final long serialVersionUID = -4856846361193249489L;

    /** The most significant 64 bits of this UUID. */
    private final long mostSigBits;

    /**  The least significant 64 bits of this UUID. */
    private final long leastSigBits;

    /** The PRNG used to generate the seed. */
    private static volatile MT19937 random = null;

    /** The delimiter of the {@link #toString() string value} */
    private static final char DELIMITER = '-';

    /**
     * Private constructor which uses a byte array to construct the new UUID.
     * 
     * @param seed the byte[] to use to generate the UUID. 
     */
    private UUID(byte[] seed) {
        Asserts.assertTrue(seed.length == 16, "The seed must contains precisely 16 bytes");
        
        long msb = 0;
        long lsb = 0;

        for (int i = 0; i <  8; i++) msb = (msb << 8) | (seed[i] & 0xff);
        for (int i = 8; i < 16; i++) lsb = (lsb << 8) | (seed[i] & 0xff);

        this.mostSigBits = msb;
        this.leastSigBits = lsb;
    }

    /**
     * Private constructor which uses two long values to construct the new UUID.
     * 
     * @param msb the most significant bits of the UUID.
     * @param lsb the least significant bits of the UUID.
     */
    private UUID(long msb, long lsb) {
        this.mostSigBits = msb;
        this.leastSigBits = lsb;
    }

    /**
     * Generates a new type 4 (mt18837) UUID using the {@link MT19937 mersenne twister}
     * engine.
     * 
     * @return a new UUID.
     */
    public static UUID generate() {
        MT19937 ng = random;
        if (ng == null) random = ng = new MT19937();

        byte[] randomBytes; ng.nextBytes(randomBytes = new byte[16]);

        randomBytes[6]  &= 0x0f;  /** clear version        */
        randomBytes[6]  |= 0x40;  /** set to version 4     */

        return new UUID(randomBytes);
    }

    /**
     * Retrieves a type 3 (name based) UUID based on the specified
     * byte array.
     *
     * @param name the byte array to use to generate the UUID.
     * @return a new UUID.
     */
    public static UUID generate(byte[] name) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new InternalError("MD5 not supported");
        }

        byte[] md5Bytes = md.digest(name);

        md5Bytes[6] &= 0x0f;  /** clear version        */
        md5Bytes[6] |= 0x30;  /** set to version 3     */

        return new UUID(md5Bytes);
    }

    /**
     * Converts a {@link String} representation of a UUID to a new UUID.
     * 
     * @param uuid the string representation of the UUID.
     * @return a new UUID.
     */
    public static UUID generate(String name) {
        String[] components = name.split("-");
        if (components.length != 5) throw new IllegalArgumentException("Invalid UUID string: " + name);
        for (int i=0; i<5; i++) components[i] = "0x"+components[i];

        long mostSigBits = Long.decode(components[0]).longValue();
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(components[1]).longValue();
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(components[2]).longValue();

        long leastSigBits = Long.decode(components[3]).longValue();
        leastSigBits <<= 48;
        leastSigBits |= Long.decode(components[4]).longValue();

        return new UUID(mostSigBits, leastSigBits);
    }

    /**
     * The version number associated with this UUID. The version number describes
     * how this UUID was generated.
     *
     * The version number has the following meaning:
     * <p><ul>
     * <li>1    Time-based UUID
     * <li>2    DCE security UUID
     * <li>3    Name-based UUID
     * <li>4    Randomly generated UUID
     * </ul>
     *
     * @return the version number of this UUID.
     */
    public int version() {
        // Version is bits masked by 0x000000000000F000 in MS long
        return (int) ((mostSigBits >> 12) & 0x0f);
    }

    /**
     * The timestamp value associated with this UUID.
     *
     * <p> The 60 bit timestamp value is constructed from the time_low,
     * time_mid, and time_hi fields of this UUID. The resulting
     * timestamp is measured in 100-nanosecond units since midnight,
     * October 15, 1582 UTC.
     *
     * @return the timestamp value of this UUID.
     * @throws UnsupportedOperationException If this UUID is not a version 1 UUID
     */
    public long timestamp() {
        if (version() == 1) return (mostSigBits & 0x0FFFL) << 48 | ((mostSigBits >> 16) & 0x0FFFFL) << 32 | mostSigBits >>> 32;
        throw new UnsupportedOperationException("Not a time-based UUID");
    }

    /**
     * The clock sequence value associated with this UUID.
     *
     * <p> The 14 bit clock sequence value is constructed from the clock
     * sequence field of this UUID.  The clock sequence field is used to
     * guarantee temporal uniqueness in a time-based UUID.
     * 
     * @return  The clock sequence of this UUID
     * @throws  UnsupportedOperationException this UUID is not a version 1 UUID
     */
    public int clockSequence() {
        if (version() == 1) return (int)((leastSigBits & 0x3FFF000000000000L) >>> 48);
        throw new UnsupportedOperationException("Not a time-based UUID");
    }

    /**
     * The node value associated with this UUID.
     *
     * <p> The 48 bit node value is constructed from the node field of this
     * UUID.  This field is intended to hold the IEEE 802 address of the machine
     * that generated this UUID to guarantee spatial uniqueness.
     *
     * @return  The node value of this UUID
     * @throws  UnsupportedOperationException If this UUID is not a version 1 UUID
     */
    public long node() {
        if (version() == 1) return leastSigBits & 0x0000FFFFFFFFFFFFL;
        throw new UnsupportedOperationException("Not a time-based UUID");
    }

    /**
     * Returns the hex value represented by the {@code loong}, to the specified number
     * of hex digits.
     * 
     * @param value the long value to convert to a hex string.
     * @param digits the number of digits to return.
     */
    private static String value(long value, int digits) {
        long hi = 1L << (digits * 4);
        return Long.toHexString(hi | (value & (hi - 1))).substring(1);
    }

    /**
     * @return The least significant 64 bits of this UUID 128 bit value
     */
    public long getLeastSignificantBits() {
        return leastSigBits;
    }

    /**
     * @return The most significant 64 bits of this UUID 128 bit value
     */
    public long getMostSignificantBits() {
        return mostSigBits;
    }

    @Override
    public int hashCode() {
        long hilo = mostSigBits ^ leastSigBits;
        return ((int)(hilo >> 32)) ^ (int) hilo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof UUID)) return false;
        UUID id = (UUID)obj;
        return (mostSigBits == id.mostSigBits && leastSigBits == id.leastSigBits);
    }

    @Override
    public int compareTo(UUID val) {
        // The ordering is intentionally set up so that the UUIDs
        // can simply be numerically compared as two numbers
        return (this.mostSigBits < val.mostSigBits   ? -1 :
               (this.mostSigBits > val.mostSigBits   ?  1 :
               (this.leastSigBits < val.leastSigBits ? -1 :
               (this.leastSigBits > val.leastSigBits ?  1 : 0))));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(value(mostSigBits  >> 32, 8) + DELIMITER);
        builder.append(value(mostSigBits  >> 16, 4) + DELIMITER);
        builder.append(value(mostSigBits  >>  0, 4) + DELIMITER);
        builder.append(value(leastSigBits >> 48, 4) + DELIMITER);
        builder.append(value(leastSigBits, 12));
        return builder.toString();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) System.out.println(UUID.generate());

        new Thread(() -> {
            for (int i = 0; i < 100; i++) System.out.println(UUID.generate());

            new Thread(() -> {
                for (int i = 0; i < 100; i++) System.out.println(UUID.generate());
            }).start();
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 100; i++) System.out.println(UUID.generate());
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 100; i++) System.out.println(UUID.generate());
        }).start();
    }

}
