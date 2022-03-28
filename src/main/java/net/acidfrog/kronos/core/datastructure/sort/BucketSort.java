package net.acidfrog.kronos.core.datastructure.sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BucketSort {

    public static void sort(int[] array) {
        int min = min(array);
        int max = max(array);
        int bucketCount = max - min + 1;

        var buckets = new ArrayList<List<Integer>>(bucketCount);

        for (int i = 0; i < bucketCount; i++) buckets.add(new ArrayList<Integer>());

        for (int value : array) {
            int index = (value - min) / bucketCount;
            buckets.get(index).add(value);
        }

        for (var bucket : buckets) Collections.sort(bucket);

        int index = 0;
        for (List<Integer> bucket : buckets) {
            for (int value : bucket) array[index++] = value;
        }
    }

    public static int min(int[] arr) {
        int min = arr[0];
        for (int value : arr) if (value < min) {
            min = value;
        }
        
        return min;
    }

    public static int max(int[] arr) {
        int max = arr[0];
        for (int value : arr) if (value > max) {
            max = value;
        }
        
        return max;
    }

}
