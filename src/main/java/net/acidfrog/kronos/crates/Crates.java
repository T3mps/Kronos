package net.acidfrog.kronos.crates;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public final class Crates {

    public static final Unsafe UNSAFE;
    
    static {
        Unsafe unsafe = null;
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
        }

        UNSAFE = unsafe;
    }
}
