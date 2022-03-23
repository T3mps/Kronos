package net.acidfrog.kronos.core.lang.assertions;

import net.acidfrog.kronos.core.lang.logger.Logger;

/**
 * Error that is thrown when an assertion fails.
 * 
 * @author Ethan Temprovich
 */
final class Assertion extends Error {

    public Assertion() {
        Logger.instance.logFatal("Assertion failed.");
        Runtime.getRuntime().halt(1);
    }

    public Assertion(boolean details) {
        Logger.instance.logFatal(String.valueOf(details));
        Runtime.getRuntime().halt(1);
    }

    public Assertion(byte details) {
        Logger.instance.logFatal(String.valueOf(details));
        Runtime.getRuntime().halt(1);
    }

    public Assertion(char details) {
        Logger.instance.logFatal(String.valueOf(details));
        Runtime.getRuntime().halt(1);
    }

    public Assertion(short details) {
        Logger.instance.logFatal(String.valueOf(details));
        Runtime.getRuntime().halt(1);
    }

    public Assertion(int details) {
        Logger.instance.logFatal(String.valueOf(details));
        Runtime.getRuntime().halt(1);
    }

    public Assertion(long details) {
        Logger.instance.logFatal(String.valueOf(details));
        Runtime.getRuntime().halt(1);
    }

    public Assertion(float details) {
        Logger.instance.logFatal(String.valueOf(details));
        Runtime.getRuntime().halt(1);
    }

    public Assertion(double details) {
        Logger.instance.logFatal(String.valueOf(details));
        Runtime.getRuntime().halt(1);
    }

    public Assertion(String message) {
        Logger.instance.logFatal(message);
        Runtime.getRuntime().halt(1);
    }

    public Assertion(Object details) {
        Logger.instance.logFatal(details.toString());
        Runtime.getRuntime().halt(1);
    }
    
}
