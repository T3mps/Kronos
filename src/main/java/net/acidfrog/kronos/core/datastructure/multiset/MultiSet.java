package net.acidfrog.kronos.core.datastructure.multiset;

import java.util.Set;

public sealed interface MultiSet<E> extends Set<E> permits Bag<E>, HashMultiset<E>, LinkedMultiset<E> {

    static final int DEFAULT_INITIAL_CAPACITY = 16;

    public abstract void set(int index, E e);

    public abstract E get(int index);

    public abstract E remove(int index);

    public abstract E pop();
    
}
