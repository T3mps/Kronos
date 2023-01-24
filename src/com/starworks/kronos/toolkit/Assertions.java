package com.starworks.kronos.toolkit;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Predicate;

public final class Assertions {

    private Assertions() {}
    
    public static void assertEquals(boolean expected, boolean actual) {
        if (expected != actual) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }

    public static void assertNotEquals(boolean expected, boolean actual) {
        if (expected == actual) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }

    public static void assertEquals(byte expected, byte actual) {
        if (expected != actual) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }

    public static void assertNotEquals(byte expected, byte actual) {
        if (expected == actual) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }
    
    public static void assertEquals(short expected, short actual) {
        if (expected != actual) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }

    public static void assertNotEquals(short expected, short actual) {
        if (expected == actual) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }
    
    public static void assertEquals(char expected, char actual) {
        if (expected != actual) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }

    public static void assertNotEquals(char expected, char actual) {
        if (expected == actual) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }
    
    public static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }

    public static void assertNotEquals(int expected, int actual) {
        if (expected == actual) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }

    public static void assertEquals(long expected, long actual) {
        if (expected != actual) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }

    public static void assertNotEquals(long expected, long actual) {
        if (expected == actual) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }
    
    public static void assertEquals(float expected, float actual) {
        if (expected != actual) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }

    public static void assertEqual(float expected, float actual, float delta) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }

    public static void assertNotEquals(float expected, float actual) {
        if (expected == actual) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }

    public static void assertNotEqual(float expected, float actual, float delta) {
        if (Math.abs(expected - actual) <= delta) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }
    
    public static void assertEquals(double expected, double actual) {
        if (expected != actual) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }

    public static void assertEqual(double expected, double actual, double delta) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }

    public static void assertNotEquals(double expected, double actual) {
        if (expected == actual) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }

    public static void assertNotEqual(double expected, double actual, double delta) {
        if (Math.abs(expected - actual) <= delta) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }

    public static <T extends Object, V extends T> void assertEquals(T expected, V actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }

    public static <T extends Object, V extends T> void assertNotEquals(T expected, V actual) {
        if (expected.equals(actual)) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }

    public static <T> void assertEquals(T expected, T actual, Comparator<T> comparator) {
        if (comparator.compare(expected, actual) != 0) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }

    public static <T> void assertNotEquals(T expected, T actual, Comparator<T> comparator) {
        if (comparator.compare(expected, actual) == 0) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }

    public static <T> void assertEquals(T[] expected, T[] actual) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected == null || actual == null) {
            throw new AssertionError("Expected: " + Arrays.toString(expected) + ", actual: " + Arrays.toString(actual));
        }
        if (expected.length != actual.length) {
            throw new AssertionError("Expected: " + Arrays.toString(expected) + ", actual: " + Arrays.toString(actual));
        }
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError("Expected: " + Arrays.toString(expected) + ", actual: " + Arrays.toString(actual));
        }
        for (int i = 0; i < expected.length; i++) {
            if (!expected[i].equals(actual[i])) {
                throw new AssertionError("Expected: " + Arrays.toString(expected) + ", actual: " + Arrays.toString(actual));
            }
        }
    }

    public static <T> void assertNotEquals(T[] expected, T[] actual) {
        if (expected == null && actual == null) {
            throw new AssertionError("Expected: " + Arrays.toString(expected) + ", actual: " + Arrays.toString(actual));
        }
        if (expected == null || actual == null) {
            return;
        }
        if (expected.length != actual.length) {
            return;
        }
        if (!Arrays.equals(expected, actual)) {
            return;
        }
        for (int i = 0; i < expected.length; i++) {
            if (!expected[i].equals(actual[i])) {
                return;
            }
        }
        throw new AssertionError("Expected: " + Arrays.toString(expected) + ", actual: " + Arrays.toString(actual));
    }

    public static <T> void assertEquals(T[] expected, T[] actual, Comparator<T> comparator) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected == null || actual == null) {
            throw new AssertionError("Expected: " + Arrays.toString(expected) + ", actual: " + Arrays.toString(actual));
        }
        if (expected.length != actual.length) {
            throw new AssertionError("Expected: " + Arrays.toString(expected) + ", actual: " + Arrays.toString(actual));
        }
        for (int i = 0; i < expected.length; i++) {
            if (comparator.compare(expected[i], actual[i]) != 0) {
                throw new AssertionError("Expected: " + Arrays.toString(expected) + ", actual: " + Arrays.toString(actual));
            }
        }
    }

    public static <T extends Object> void assertNull(T value) {
        if (value != null) {
            throw new AssertionError("Expected: null, actual: " + value);
        }
    }

    public static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected: true, actual: false");
        }
    }

    public static <T> void assertTrue(T value, Predicate<T> predicate) {
        if (!predicate.test(value)) {
            throw new AssertionError("Expected: true, actual: false");
        }
    }

    public static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected: false, actual: true");
        }
    }

    public static <T> void assertFalse(T value, Predicate<T> predicate) {
        if (predicate.test(value)) {
            throw new AssertionError("Expected: false, actual: true");
        }
    }

    public static <T extends Throwable> void assertThrows(Class<T> expected, Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable actual) {
            if (!expected.isInstance(actual)) {
                throw new AssertionError("Expected: " + expected + ", actual: " + actual);
            }
            return;
        }
        throw new AssertionError("Expected: " + expected + ", actual: none");
    }

    public static <T extends Throwable> void assertThrows(Class<T> expected, Runnable runnable, Predicate<T> predicate) {
        try {
            runnable.run();
        } catch (Throwable actual) {
            if (!expected.isInstance(actual)) {
                throw new AssertionError("Expected: " + expected + ", actual: " + actual);
            }
            if (!predicate.test(expected.cast(actual))) {
                throw new AssertionError("Expected: " + expected + ", actual: " + actual);
            }
            return;
        }
        throw new AssertionError("Expected: " + expected + ", actual: none");
    }

    public static void assertTimeout(long timeout, Runnable runnable) {
        long start = System.currentTimeMillis();
        runnable.run();
        long end = System.currentTimeMillis();
        if (end - start > timeout) {
            throw new AssertionError("Expected: " + timeout + ", actual: " + (end - start));
        }
    }

    public static void assertTimeout(long timeout, Runnable runnable, Predicate<Throwable> predicate) {
        long start = System.currentTimeMillis();
        try {
            runnable.run();
        } catch (Throwable actual) {
            if (!predicate.test(actual)) {
                throw new AssertionError("Expected: " + timeout + ", actual: " + (System.currentTimeMillis() - start));
            }
            return;
        }
        long end = System.currentTimeMillis();
        if (end - start > timeout) {
            throw new AssertionError("Expected: " + timeout + ", actual: " + (end - start));
        }
    }
}
