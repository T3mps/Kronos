package com.starworks.kronos.toolkit;

import java.text.DecimalFormat;
import java.util.Objects;

public final class Chrono {

    public static enum TimeUnit {
        
        NANOSECONDS(1L, "ns"),
        MICROSECONDS(1000L * NANOSECONDS.scale, "us"),
        MILLISECONDS(1000L * MICROSECONDS.scale, "ms"),
        SECONDS(1000L * MILLISECONDS.scale, "s"),
        MINUTES(60L * SECONDS.scale, "m"),
        HOURS(60L * MINUTES.scale, "h"),
        DAYS(24L * HOURS.scale, "d");

        private final long scale;
        private final String symbol;

        private TimeUnit(long s, String ss) {
            this.scale = s;
            this.symbol = ss;
        }
        
        private static long cvt(long d, long dst, long src) {
            long r, m;
            
            if (src == dst) {
                return d;
            } else if (src < dst) {
                return d / (dst / src);
            } else if (d > (m = Long.MAX_VALUE / (r = src / dst))) {
                return Long.MAX_VALUE;
            } else if (d < -m) {
                return Long.MIN_VALUE;
            } else {
                return d * r;
            }
        }

        long convert(long sourceDuration, TimeUnit sourceUnit) {
            switch (this) {
                case NANOSECONDS:  return sourceDuration;
                case MICROSECONDS: return cvt(sourceDuration, MICROSECONDS.scale, sourceUnit.scale);
                case MILLISECONDS: return cvt(sourceDuration, MILLISECONDS.scale, sourceUnit.scale);
                case SECONDS:      return cvt(sourceDuration, SECONDS.scale, sourceUnit.scale);
                case MINUTES:      return cvt(sourceDuration, MINUTES.scale, sourceUnit.scale);
                case HOURS:        return cvt(sourceDuration, HOURS.scale, sourceUnit.scale);
                case DAYS:         return cvt(sourceDuration, DAYS.scale, sourceUnit.scale);
                default:    throw new AssertionError("Unknown TimeUnit");
            }
        }

        public long scale() {
            return scale;
        }

        public String symbol() {
            return symbol;
        }
    }

    // hidden constructor
    private Chrono() {
    }

    public static final double convert(double duration, TimeUnit targetUnit, TimeUnit sourceUnit) {
        return convert((long) duration, targetUnit, sourceUnit);
    }

    public static final long convert(long duration, TimeUnit targetUnit, TimeUnit sourceUnit) {
        Objects.requireNonNull(sourceUnit);
        Objects.requireNonNull(targetUnit);
        return sourceUnit.convert(duration, targetUnit);
    }

    public static final long now() {
        return System.nanoTime();
    }

    public static final long nanos() {
        return System.nanoTime();
    }

    public static final long micros() {
        return System.nanoTime() / 1000L;
    }

    public static final long millis() {
        return System.currentTimeMillis();
    }

    public static final VirtualTimer createVirtualTimer() {
        return new VirtualTimer();
    }

    public static final VirtualTimer createVirtualTimer(long start) {
        return new VirtualTimer(start);
    }

    public static record Time(long value, TimeUnit unit) implements Comparable<Time> {

        public static final Time INVALID = new Time(-1, TimeUnit.NANOSECONDS);
        public static final Time ZERO = new Time(0, TimeUnit.NANOSECONDS);

        private static final DecimalFormat DP3 = new DecimalFormat("#.###");

        public Time(long value, TimeUnit unit) {
            this.value = value;
            this.unit = Objects.requireNonNull(unit);
        }

        public Time convert(TimeUnit to) {
            return new Time(Chrono.convert(value, unit, to), to);
        }

        @Override
        public int compareTo(Time o) {
            if (value < o.value) {
                return -1;
            }
            if (value > o.value) {
                return 1;
            }
            return 0;
        }

        @Override
        public String toString() {
            return DP3.format(value) + unit.symbol;
        }
    }

    public static final class VirtualTimer {

        private long start; // nanos
        private long end; // nanos
        private long elapsed; // nanos
        private boolean running;

        VirtualTimer() {
            this(0);
        }

        VirtualTimer(long start) {
            this.start = start;
            this.end = start;
            this.elapsed = 0;
            this.running = false;
        }

        public VirtualTimer start() {
            if (!running) {
                start = (start == 0) ? Chrono.now() : start;
                running = true;
            }
            
            return this;
        }

        public VirtualTimer stop() {
            if (running) {
                end = Chrono.now();
                elapsed = end - start;
                running = false;
            }

            return this;
        }

        public VirtualTimer reset() {
            start = 0;
            end = 0;
            elapsed = 0;
            running = false;

            return this;
        }

        public VirtualTimer restart() {
            reset();
            start();

            return this;
        }

        public Time elapsed() {
            if (running) {
                return new Time(Chrono.now() - start, TimeUnit.NANOSECONDS);
            }

            return new Time(elapsed, TimeUnit.NANOSECONDS);
        }

        public Time elapsed(TimeUnit unit) {
            return new Time(Chrono.convert(elapsed().value, unit, TimeUnit.NANOSECONDS), unit);
        }
    }
}
