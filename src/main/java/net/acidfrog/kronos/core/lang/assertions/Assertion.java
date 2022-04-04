package net.acidfrog.kronos.core.lang.assertions;

import net.acidfrog.kronos.core.lang.logger.Logger;

/**
 * Error that is thrown when an assertion fails.
 * 
 * @author Ethan Temprovich
 */
final class Assertion extends Error {

    public Assertion() {
        Logger.logFatal("Assertion failed.");
        Runtime.getRuntime().halt(1);
    }

    public Assertion(boolean details) {
        Logger.logFatal(String.valueOf(details));
        Runtime.getRuntime().halt(1);
    }

    public Assertion(byte details) {
        Logger.logFatal(String.valueOf(details));
        Runtime.getRuntime().halt(1);
    }

    public Assertion(char details) {
        Logger.logFatal(String.valueOf(details));
        Runtime.getRuntime().halt(1);
    }

    public Assertion(short details) {
        Logger.logFatal(String.valueOf(details));
        Runtime.getRuntime().halt(1);
    }

    public Assertion(int details) {
        Logger.logFatal(String.valueOf(details));
        Runtime.getRuntime().halt(1);
    }

    public Assertion(long details) {
        Logger.logFatal(String.valueOf(details));
        Runtime.getRuntime().halt(1);
    }

    public Assertion(float details) {
        Logger.logFatal(String.valueOf(details));
        Runtime.getRuntime().halt(1);
    }

    public Assertion(double details) {
        Logger.logFatal(String.valueOf(details));
        Runtime.getRuntime().halt(1);
    }

    public Assertion(String message) {
        Logger.logFatal(message);
        Runtime.getRuntime().halt(1);
    }

    public Assertion(Object details) {
        Logger.logFatal(details.toString());
        Runtime.getRuntime().halt(1);
    }
    
}
