package net.acidfrog.kronos.core;

public final class Chrono {

    private static final long NANOS_PER_MILLI = 1_000_000;

    private Chrono() {}

    public static long now() {
        return System.nanoTime();
    }
    
    public static long nowMillis() {
        return System.currentTimeMillis();
    }

    public static long nanosToMillis(long nanos) {
        return nanos / NANOS_PER_MILLI;
    }

    public static long millisToNanos(long millis) {
        return millis * NANOS_PER_MILLI;
    }

    public static long timeSinceNanos(long prev) {
        return now() - prev;
    }

    public static long timeSinceMillis(long prev) {
        return nowMillis() - prev;
    }

}
