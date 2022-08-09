package test.toolkit;

public class Main {
    
    public static int sieve(int n) {
        if (n <= 1.0) {
            return 0;
        }

        int count = 0;
        for (int i = 2; i <= n; i++) {
            if (isPrime(i)) {
                count++;
            }
        }
        return count;
    }

    public static boolean isPrime(int n) {
        if (n == 2) {
            return true;
        }
        if (n % 2 == 0) {
            return false;
        }
        
        double sqrt = Math.sqrt(n);
        for (int i = 3; i <= sqrt; i += 2) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        // var b = Benchmark.prepare(Main.class, new Main());
        // b.run(1024, "sieve", 100);
        // b.run(1024, "isPrime", 1000);

        // Benchmark.prepare(Main.class, new Main()).simulate(10000, "sieve");
    }
}
