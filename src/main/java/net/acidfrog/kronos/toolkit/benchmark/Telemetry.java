package net.acidfrog.kronos.toolkit.benchmark;

import net.acidfrog.kronos.toolkit.Chrono.Time;

public final class Telemetry implements Comparable<Telemetry>, Cloneable {

    private String name;
    private int iterations;
    private Time shortestRuntime;
    private Time longestRuntime;
    private Time averageRuntime;
    private Time totalRuntime;

    Telemetry(String name, int iterations, Time shortestRuntime, Time longestRuntime, Time averageRuntime, Time totalRuntime) {
        set(name, iterations, shortestRuntime, longestRuntime, averageRuntime, totalRuntime);
    }

    Telemetry() {
        this("", 0, Time.ZERO, Time.ZERO, Time.ZERO, Time.ZERO);
    }

    void set(String name, int iterations, Time shortestRuntime, Time longestRuntime, Time averageRuntime, Time totalRuntime) {
        this.name = name;
        this.iterations = iterations;
        this.shortestRuntime = shortestRuntime;
        this.longestRuntime = longestRuntime;
        this.averageRuntime = averageRuntime;
        this.totalRuntime = totalRuntime;
    }

    public String getName() {
        return name;
    }

    public int getIterations() {
        return iterations;
    }

    public Time shortestRuntime() {
        return shortestRuntime;
    }

    public Time longestRuntime() {
        return longestRuntime;
    }

    public Time averageRuntime() {
        return averageRuntime;
    }

    public Time totalRuntime() {
        return totalRuntime;
    }

    long comparableRuntime() {
        return totalRuntime.value();
    }

    @Override
    public int compareTo(Telemetry other) {
        return totalRuntime.compareTo(other.totalRuntime);
    }

    @Override
    public Telemetry clone() {
        return new Telemetry(name, iterations, shortestRuntime, averageRuntime, longestRuntime, totalRuntime);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(" executed ");
        sb.append(iterations);
        sb.append(" times [shortest: ");
        sb.append(shortestRuntime);
        sb.append(", longest: ");
        sb.append(longestRuntime);
        sb.append(", average: ");
        sb.append(averageRuntime);
        sb.append(", total: ");
        sb.append(totalRuntime);
        sb.append("]");
        return sb.toString();
    }
}
