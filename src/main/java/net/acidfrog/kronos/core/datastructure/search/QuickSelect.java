package net.acidfrog.kronos.core.datastructure.search;

import java.util.Random;

import net.acidfrog.kronos.mathk.random.MT19937;

public final class QuickSelect {

    private static final Random random = new MT19937();

    private static int[] unsorted = null;
    private static int[] temp = null;

    private QuickSelect() {}

    public static int find(int value, int[] array) {
        unsorted = array;
        temp = new int[unsorted.length];
        try {
            int tempLength = unsorted.length;
            int length = tempLength;
            int pivot = unsorted[0];
            while (length > 0) {
                length = tempLength;
                pivot = unsorted[random.nextInt(length)];
                tempLength = 0;
                for (int i = 0; i < length; i++) {
                    int iValue = unsorted[i];
                    if (value == iValue)
                        return i;
                    else if (value > pivot && iValue > pivot)
                        temp[tempLength++] = iValue;
                    else if (value < pivot && iValue < pivot)
                        temp[tempLength++] = iValue;
                }
                unsorted = temp;
                length = tempLength;
            }
            return Integer.MAX_VALUE;
        } finally {
            QuickSelect.unsorted = null;
            QuickSelect.temp = null;
        }
    }
    
}
