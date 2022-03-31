package net.acidfrog.kronos.core.datastructure.multimap;

import java.util.Map;

public interface MultiMap<K, V> extends Map<K, V> {
    
    boolean unmap(K key, V value);
    
}
