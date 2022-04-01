package net.acidfrog.kronos.core.datastructure.algorithm.sort;

import java.util.Comparator;

import net.acidfrog.kronos.core.datastructure.multiset.Bag;

public final class Sort {

    private Sort() {}

    public static <T extends Comparable<T>> void tim(T[] array) {
        tim(array, new Comparator<T>() {

            @Override
            public int compare(T o1, T o2) {
                return o1.compareTo(o2);
            }
        });
    }

    public static <T> void tim(T[] array, Comparator<T> comparator) {
        TimSort.sort(array, comparator);
    }

    public static <T extends Comparable<T>> void tim(Bag<T> bag) {
        tim(bag, new Comparator<T>() {

            @Override
            public int compare(T o1, T o2) {
                return o1.compareTo(o2);
            }
        });
    }

    public static <T> void tim(Bag<T> bag, Comparator<T> comparator) {
        TimSort.sort(bag.data, comparator);
    }
    
}
