package net.acidfrog.kronos.core.lang.sort;

public final class RadixSort implements Sorter {

    private final Sorter cs = new CountSort();

    public RadixSort() {}

    public void sort(int[] array) {
        int max = array[0];
        for (int i = 1; i < array.length; i++) if (array[i] > max) {
            max = array[i];
        }

        for (int exp = 1; max / exp > 0; exp *= MAX_COUNT) cs.sort(array);
    }

}
