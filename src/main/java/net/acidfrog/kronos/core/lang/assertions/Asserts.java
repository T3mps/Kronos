package net.acidfrog.kronos.core.lang.assertions;

public final class Asserts {

    public static final void assertTrue(final boolean condition, final String message) {
        if (condition) return;
        else throw new Assertion(message);
    }

    public static final void assertFalse(final boolean condition, final String message) {
        if (condition) throw new Assertion(message);
        else return;
    }

    public static final void assertEquals(final Object a, final Object b, final String message) {
        if (a == null || b == null) throw new Assertion(message);
        if (a.equals(b))  return;
        else throw new Assertion(message);
    }

    public static final void assertNotEquals(final Object a, final Object b, final String message) {
        if (a == null || b == null) throw new Assertion(message);
        if (a.equals(b)) throw new Assertion(message);
        else return;
    }

    public static final void assertNotNull(final Object object, final String message) {
        if (object == null) throw new Assertion(message);
        else return;
    }

    public static final void assertNull(final Object object, final String message) {
        if (object == null) return;
        else throw new Assertion(message);
    }
    
}
