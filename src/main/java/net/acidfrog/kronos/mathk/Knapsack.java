package net.acidfrog.kronos.mathk;

public final class Knapsack {

    @SuppressWarnings("unchecked")
    public static final <T extends Comparable<T>> T[] get(T[] comparables, float[] weights, int capacity) {
        int n = comparables.length;
        if (n != weights.length) throw new IllegalArgumentException("Arrays must be of the same length");

        Item<T>[] items = new Item[n];

        for (int i = 0; i < n; i++) items[i] = new Item<T>(comparables[i], weights[i]);

        return get(items, capacity);
    }

    @SuppressWarnings("unchecked")
    public static final <T extends Comparable<T>> T[] get(Item<T>[] items, int capacity) {
        if (capacity < 0) throw new IllegalArgumentException("Capacity must be positive");

        T[] result = (T[]) new Comparable[items.length];
        T[] best = (T[]) new Comparable[items.length];
        T[] prev = (T[]) new Comparable[items.length];

        for (int i = 0; i < items.length; i++) {
            prev[i] = items[i].getValue();
            best[i] = items[i].getValue();
        }

        for (int i = 0; i < items.length; i++) {
            for (int j = 0; j < i; j++) if (items[j].getWeight() <= items[i].getWeight()) {
                prev[i] = prev[j].compareTo(prev[i]) > 0 ? prev[j] : prev[i];
                best[i] = best[j].compareTo(best[i]) > 0 ? best[j] : best[i];
            }
        }

        for (int i = 0; i < items.length; i++) if (items[i].getWeight() <= capacity) {
            result[i] = prev[i].compareTo(best[i]) > 0 ? best[i] : prev[i];
        } else {
            result[i] = best[i];
        }

        return result;
    }

    public static final int[] get(int[] values, int weights[], int capacity) {
        if (values.length != weights.length) throw new IllegalArgumentException("Arrays must be of the same length");
        if (capacity < 0) throw new IllegalArgumentException("Capacity must be positive");

        int[] result = new int[values.length];
        int[] best = new int[values.length];
        int[] prev = new int[values.length];
        
        for (int i = 0; i < values.length; i++) {
            if (weights[i] <= capacity) {
                best[i] = values[i];
                prev[i] = i;
            }
        }

        for (int i = 1; i < values.length; i++) {
            int bestWeight = 0;
            int bestValue = 0;
            int bestIndex = 0;
            for (int j = 0; j < values.length; j++) {
                if (weights[j] > capacity) {
                    continue;
                }
                if (bestWeight + weights[j] <= capacity) {
                    int value = best[j] + values[j];
                    if (value > bestValue) {
                        bestValue = value;
                        bestWeight = bestWeight + weights[j];
                        bestIndex = j;
                    }
                }
            }
            best[i] = bestValue;
            prev[i] = bestIndex;
        }
        
        int bestIndex = values.length - 1;
        while (bestIndex > 0) {
            result[bestIndex] = 1;
            bestIndex = prev[bestIndex];
        }
        return result;
    }

    public static final class Item<T extends Comparable<T>> {
        
        private final T value;
        private final float weight;

        public Item(T value, float weight) {
            this.value = value;
            this.weight = weight;
        }

        public T getValue() {
            return value;
        }

        public float getWeight() {
            return weight;
        }

    }   
    
}
