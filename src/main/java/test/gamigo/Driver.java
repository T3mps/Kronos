package test.gamigo;

import java.text.NumberFormat;
import java.util.Locale;

public class Driver {
    
    public static void run(String name, Runnable runnable, int iterations) {
        long sum = 0;
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            runnable.run();
            long end = System.nanoTime();
            sum += end - start;
        }

        System.out.println("#\tResults from " + name);
        System.out.println("#\tSimulation ran " + NumberFormat.getInstance(Locale.US).format(iterations) + " times");
        System.out.println("#\tAverage time: " + (sum / iterations) + "ns");
        System.out.println("#\tTotal time: " + String.format("%.3fms", (sum / 1_000_000.0)));
        System.out.println("#");
    }
}
