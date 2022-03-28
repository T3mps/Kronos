package net.acidfrog.kronos.core.datastructure.sort;

public final class CountSort {
    
    static final int MAX_COUNT = 10;

    private CountSort() {}

    private static void internalSort(int[] arr, int n, int exp) {
        int[] output = new int[n];
        int[] count = new int[MAX_COUNT];
        int i;

        for (i = 0; i < MAX_COUNT; i++) count[i] = 0;

        for (i = 0; i < n; i++) count[(arr[i] / exp) % MAX_COUNT]++;

        for (i = 1; i < MAX_COUNT; i++) count[i] += count[i - 1];

        for (i = n - 1; i >= 0; i--) {
            output[count[(arr[i] / exp) % MAX_COUNT] - 1] = arr[i];
            count[(arr[i] / exp) % MAX_COUNT]--;
        }

        for (i = 0; i < n; i++) arr[i] = output[i];
    }

    public static void sort(int[] array) {     
        int n = array.length;
        int exp = 1;
        int i;

        for (i = 0; i < n; i++) {
            internalSort(array, n, exp);
            exp *= MAX_COUNT;
        }

        for (i = 0; i < n; i++) {
            internalSort(array, n, exp);
            exp *= MAX_COUNT;
        }
    }

}
