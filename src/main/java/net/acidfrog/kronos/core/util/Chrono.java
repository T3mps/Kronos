package net.acidfrog.kronos.core.util;

import java.util.concurrent.atomic.AtomicLong;

import net.acidfrog.kronos.core.lang.logger.Logger;

public final class Chrono {

    private static final int NANOS_PER_MICRO     =   1000;

    private static final int NANOS_PER_MILLI     =   1_000_000;

    private static final int NANOS_PER_SECOND    =   1_000_000_000;

    private static final long NANOS_PER_MINUTE    =   60_000_000_000L;

    private static final long NANOS_PER_HOUR      =   3_600_000_000_000L;

    private static final int MILLIS_PER_MINUTE  =   60_000;

    private static final int MILLIS_PER_HOUR    =   3_600_000;

    private Chrono() {}

    public static final long now() {
        return System.nanoTime();
    }

    public static final long nowMillis() {
        return System.currentTimeMillis();
    }

    public static final long nanoseconds() {
        return System.nanoTime();
    }

    public static final long microseconds() {
        return System.nanoTime() / NANOS_PER_MICRO;
    }

    public static final long milliseconds() {
        return System.nanoTime() / NANOS_PER_MILLI;
    }

    public static final long seconds() {
        return System.nanoTime() / NANOS_PER_SECOND;
    }

    public static final long minutes() {
        return System.nanoTime() / NANOS_PER_MINUTE;
    }

    public static final long hours() {
        return System.nanoTime() / NANOS_PER_HOUR;
    }

    public static final long nanoToMicro(long nano) {
        return nano / NANOS_PER_MICRO;
    }

    public static final long nanoToMilli(long nano) {
        return nano / NANOS_PER_MILLI;
    }

    public static final long nanoToSecond(long nano) {
        return nano / NANOS_PER_SECOND;
    }

    public static final long nanoToMinute(long nano) {
        return nano / NANOS_PER_MINUTE;
    }

    public static final long nanoToHour(long nano) {
        return nano / NANOS_PER_HOUR;
    }

    public static final long microToNano(long micro) {
        return micro * NANOS_PER_MICRO;
    }

    public static final long microToMilli(long micro) {
        return micro * NANOS_PER_MICRO;
    }

    public static final long microToSecond(long micro) {
        return micro * NANOS_PER_MICRO;
    }

    public static final long microToMinute(long micro) {
        return micro * MILLIS_PER_MINUTE;
    }

    public static final long microToHour(long micro) {
        return micro * MILLIS_PER_HOUR;
    }

    public static final long milliToNano(long milli) {
        return milli * NANOS_PER_MILLI;
    }

    public static final long milliToMicro(long milli) {
        return milli * NANOS_PER_MICRO;
    }

    public static final long milliToSecond(long milli) {
        return milli * NANOS_PER_MILLI;
    }

    public static final long milliToMinute(long milli) {
        return milli * MILLIS_PER_MINUTE;
    }

    public static final long milliToHour(long milli) {
        return milli *  MILLIS_PER_HOUR;
    }

    public static final long secondToNano(long second) {
        return second * NANOS_PER_SECOND;
    }

    public static final long secondToMicro(long second) {
        return second * NANOS_PER_SECOND;
    }

    public static final long secondToMilli(long second) {
        return second * NANOS_PER_SECOND;
    }

    public static final long secondToMinute(long second) {
        return second * MILLIS_PER_MINUTE;
    }

    public static final long secondToHour(long second) {
        return second * MILLIS_PER_HOUR;
    }

    public static final long minuteToNano(long minute) {
        return minute * NANOS_PER_MINUTE;
    }

    public static final long minuteToMicro(long minute) {
        return minute * MILLIS_PER_MINUTE;
    }

    public static final long minuteToMilli(long minute) {
        return minute * MILLIS_PER_MINUTE;
    }

    public static final long minuteToSecond(long minute) {
        return minute * MILLIS_PER_MINUTE;
    }

    public static final long minuteToHour(long minute) {
        return minute * MILLIS_PER_HOUR;
    }

    public static final long hourToNano(long hour) {
        return hour * NANOS_PER_HOUR;
    }

    public static final long hourToMicro(long hour) {
        return hour * MILLIS_PER_HOUR;
    }

    public static final long hourToMilli(long hour) {
        return hour * MILLIS_PER_HOUR;
    }

    public static final long hourToSecond(long hour) {
        return hour * MILLIS_PER_HOUR;
    }

    public static final long hourToMinute(long hour) {
        return hour * MILLIS_PER_HOUR;
    }

    public static final class Clock {

        private final AtomicLong start;
        private final AtomicLong end;

        public Clock() {
            this.start = new AtomicLong(0L);
            this.end   = new AtomicLong(0L);
        }

        public Clock(boolean startOnInstantiation) {
            this.start = new AtomicLong(startOnInstantiation ? System.nanoTime() : 0L);
            this.end   = new AtomicLong(0L);
        }

        public Clock(Clock clock) {
            this.start = new AtomicLong(clock.start.get());
            this.end   = new AtomicLong(clock.end.get());
        }

        public Clock start() {
            this.start.set(Chrono.now());
            this.end.set(0L);
            return this;
        }

        public Clock stop() {
            this.end.set(Chrono.now());
            return this;
        }

        public Clock reset() {
            if (end.get() != 0L) {
                Clock clock = new Clock(this);
                start.set(0L);
                end.set(0L);
                return clock;
            }

            Logger.instance.logWarn("Clock cannot be reset while it is running.");
            return this;
        }

        public long getStart() {
            return start.get();
        }

        public long getEnd() {
            return end.get();
        }

        public long duration() {
            if (end.get() == 0L) return Chrono.now() - start.get();
            return end.get() - start.get();
        }

        public long durationMillis() {
            return Chrono.nanoToMilli(duration());
        }

        public long durationMicros() {
            return Chrono.nanoToMicro(duration());
        }

        public long durationSeconds() {
            return Chrono.nanoToSecond(duration());
        }

        public long durationMinutes() {
            return Chrono.nanoToMinute(duration());
        }

        public long durationHours() {
            return Chrono.nanoToHour(duration());
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Clock [start=")
              .append(start)
              .append(", end=")
              .append(end)
              .append(", duration=")
              .append(durationSeconds())
              .append("]");
            return sb.toString();
        }

    }

}
