package test.gamigo;

public class Problem2 {
    
    /*
     * Brute force approach
     */
    public static String sortLettersBruteForce(String input, String sortOrder) {
        String result = "";

        for (int i = 0; i < sortOrder.length(); i++) {
            for (int j = 0; j < input.length(); j++) {
                if (sortOrder.charAt(i) == input.charAt(j)) {
                    result += input.charAt(j);
                }
            }
        }

        return result;
    }

    /*
     * Optimized approach
     */
    public static String sortLettersOptimized(String input, String sortOrder) {
        String result = "";
        int[] count = new int[256];

        input = input.toLowerCase();
        sortOrder = sortOrder.toLowerCase();

        for (int i = 0; i < input.length(); i++) {
            count[input.charAt(i)]++;
        }

        for (int i = 0; i < sortOrder.length(); i++) {
            char index = sortOrder.charAt(i);

            if (count[index] > 0) {
                for (int j = 0; j < count[index]; j++) {
                    result += index;
                }
            }
        }

        return result;
    }
    
    public static void main(String[] args) {
        String[] tests = { "trion world network", "hello", "world", "Ethan Temprovich", "Professor Ambrose" };
        String[] orders = { " oinewkrtdl", "lheo", "dlrow", "vtempaonich", "Profes amb" };
        int iterations = 100_000;

        for (int i = 0; i < tests.length; i++) {
            String input = tests[i];
            String order = orders[i];
            String result = sortLettersOptimized(input, order);

            System.out.println("Order: " + order);
            System.out.println(input + " -> " + result + "\n");
        }

        Driver.run("Brute Force", () -> sortLettersBruteForce(tests[0], orders[0]), iterations);
        Driver.run("--Optimized", () -> sortLettersOptimized(tests[0], orders[0]), iterations);

        System.out.println("\n\nDue to the nature of the JVM, these results are not very accurate, but still a good indication of the performance compared to each other.");
    }
}
