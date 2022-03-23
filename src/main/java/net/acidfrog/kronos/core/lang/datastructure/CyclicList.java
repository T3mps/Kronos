package net.acidfrog.kronos.core.lang.datastructure;

import java.util.LinkedList;

import net.acidfrog.kronos.mathk.Mathk;

public class CyclicList<E> implements Cloneable {

    private final LinkedList<E> data;

    private E head;
    private E tail;

    public CyclicList() {
        this.data = new LinkedList<E>();
        this.head = null;
        this.tail = null;
    }
    
    public E get(int index) {
        return data.get(cyclic(index));
    }

    public void add(E e) {
        data.add(e);
        cycle();
    }

    public void remove(E e) {
        data.remove(e);
        cycle();
    }
    
    public void remove(int index) {
        index = cyclic(index);
        data.remove(index);
        cycle();
    }

    int cyclic(int index) {
        int size = data.size();
        if (size == 0) return -1;

        int offset = size - index;
        if (offset < 0) index = Mathk.abs(offset);
        return offset;
    }

    void cycle() {
        this.head = data.get(0);
        this.tail = data.get(data.size() - 1);
    }

    public int size() {
        return data.size();
    }

    public E getHead() {
        return head;
    }

    public E getTail() {
        return tail;
    }

    @Override
    public CyclicList<E> clone() {
        CyclicList<E> clone = new CyclicList<E>();
        for (E e : data) clone.add(e);
        return clone;
    }

}
