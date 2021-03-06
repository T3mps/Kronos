package net.acidfrog.kronos.core.util;

import java.util.concurrent.atomic.AtomicInteger;

import net.acidfrog.kronos.core.lang.annotations.Internal;
import net.acidfrog.kronos.core.lang.error.KronosError;
import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;

/**
 * This class is used to assign unsigned IDs. At runtime an array
 * of unsigned integers from {@value 0} to {@value 16,777,216 (2^24)}
 * will be cached. An atomic pointer will keep track of which index
 * to assign next. The first ID assigned will always be {@value 0}.
 * This class is threadsafe.
 * 
 * @author Ethan Temprovich
 */
@Internal
public final class IDArbiter {
    
    /** 2^24 | 16777216 */
    public static final int MAX_ID_COUNT = 1 << 24 ;
     
    /** The cached ids. */
    private final int[] cache = new int[MAX_ID_COUNT];
    

    /**
     * The atomic pointer, which holds the current index. Starts at -1
     * because the first ID is {@value 0}.
     * 
     * @see #next()
     */
    private volatile AtomicInteger pointer = new AtomicInteger(-1);

    /** Hidden constructor. */
    public IDArbiter() {
        for (int i = 0; i < MAX_ID_COUNT; i++) cache[i] = i;
    }

    /**
     * Moves the {@link #pointer} to the next availible id and returns it.
     * 
     * @return the next availible id.
     */
    public int next() {
        synchronized (this) {
            if (pointer.intValue() + 1 < MAX_ID_COUNT) {
                return cache[pointer.incrementAndGet()];
            } throw new KronosError(KronosErrorLibrary.INDEX_OUT_OF_BOUNDS);
        }
    }

    /**
     * Resets the state of the {@link #pointer} to {@code -1}.
     */
    public void reset() {
        pointer.set(-1);
    }

}
