package test.gamigo;

public class Problem1 {

    public static boolean allDigitsUnique(int n) {
        boolean[] digits = new boolean[10];
        while (n > 0) {
            int digit = n % 10;
            if (digits[digit]) {
                return false;
            }
            digits[digit] = true;
            n /= 10;
        }

        return true;
    }

    public static void main(String[] args) {
        System.out.println(allDigitsUnique(4877_8584));
        System.out.println(allDigitsUnique(1730_8459));
    }
}
