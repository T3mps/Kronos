package net.acidfrog.kronos.core.lang.sort;

public sealed interface Sorter permits CountSort, RadixSort, QuickSort, BucketSort {

    static final int MAX_COUNT = 10;

    public void sort(int[] array);
    
}
