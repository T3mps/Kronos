package net.acidfrog.kronos.math;

// https://dyn4j.org/
public final class Epsilon {
		
    public static final double E = compute();

    private Epsilon() { }

    /**
     * Computes an approximation of machine epsilon.
     * 
     * @return double
     */
    public static final double compute() {
        double e = 0.5;
        while (1.0 + e > 1.0) e *= 0.5;
        return e;
    }

}
