package net.acidfrog.kronos.core.datastructure.array;

import java.util.Comparator;

public sealed interface Array<E> permits DynamicArray<E> {

    public E add(E value);

    public void addAll(Array<E> array);

    public void addAll(E[] array);

    public E set(int index, E value);

    public E push(E value);

    public E get(int index);

    public boolean contains(E value);

    public boolean contains(E value, boolean identity);

    public int indexOf(E value);

    public int lastIndexOf(E value);

    public void remove(int index);

    public void remove(int index, int count);

    public E remove(E value);

    public E pop();

    public void clear();

    public void resize(int newSize);

    public void sort(Comparator<E> comparator);

    public void reverse();
    
    public void shuffle();

    public void shuffle(int times);

    public int size();

    public int length();

    public E[] toArray();
    
}
