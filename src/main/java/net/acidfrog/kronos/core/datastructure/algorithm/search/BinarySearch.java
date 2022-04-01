package net.acidfrog.kronos.core.datastructure.algorithm.search;

public final class BinarySearch {

    private static final int BRUTE_FORCE_THRESHOLD = 256;
    
    private static int[] array = null;

    private BinarySearch() {}

    public static int find(int value, int[] array) {
        BinarySearch.array = array;
        return find(value, 0, array.length - 1);
    }

    private static int find(int value, int start, int end) {
        if (start == end) {
            int lastValue = array[start]; // start==end
            if (value == lastValue) return start; // start==end
            return Integer.MAX_VALUE;
        }
        if (end - start <= BRUTE_FORCE_THRESHOLD) return linearSearch(value, start, end);
        
        int mid = (start + end) / 2;
        if (array[mid] == value) return mid;
        else if (array[mid] > value) return find(value, start, mid - 1);
        
        return find(value, mid + 1, end);
    }

    public static int upperBound(int[] array, int length, int value) {
        int low = 0;
        int high = length;
        while (low < high) {
            final int mid = (low + high) / 2;
            if (value >= array[mid]) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return low;
    }

    public static int lowerBound(int[] array, int length, int value) {
        int low = 0;
        int high = length;
        while (low < high) {
            final int mid = (low + high) / 2;
            //checks if the value is less than middle element of the array
            if (value <= array[mid]) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }
        return low;
    }

    private static final int linearSearch(int value, int start, int end) {
        for (int i = start; i < end; i++) if (array[i] == value) {
            return i;
        }

        return -1;
    }
    
}
