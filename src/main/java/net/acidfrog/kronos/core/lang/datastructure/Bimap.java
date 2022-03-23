package net.acidfrog.kronos.core.lang.datastructure;

import net.acidfrog.kronos.core.lang.Std;

public class Bimap<K, A, B> {
    
    private K key;
    private Std.Pair<A, B> value;

    private Std.Pair<A, Std.Pair<A, B>>[] data;

}
