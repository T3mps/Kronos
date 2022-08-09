package net.acidfrog.kronos.crates;

import java.util.Iterator;
import java.util.stream.Stream;

public interface Container<T> extends Iterable<T> {
    
    public T get(int index);
    
    public int size();
    
    public boolean isEmpty();
    
    @Override
    public Iterator<T> iterator();
    
    public Stream<T> stream();
    
    public Stream<T> parallelStream();
    
}
