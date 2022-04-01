package net.acidfrog.kronos.core.datastructure.algorithm.sort;

public final class RadixSort {

    public RadixSort() {}

    public void sort(int[] array) {
        int max = array[0];
        for (int i = 1; i < array.length; i++) if (array[i] > max) {
            max = array[i];
        }

        for (int exp = 1; max / exp > 0; exp *= CountSort.MAX_COUNT) CountSort.sort(array);
    }

}
