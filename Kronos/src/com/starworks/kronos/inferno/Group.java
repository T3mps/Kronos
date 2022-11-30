package com.starworks.kronos.inferno;

import java.util.Iterator;
import java.util.stream.Stream;

public interface Group<T> extends Iterable<T> {

    public abstract Group<T> include(Class<?>... componentTypes);
    
    public abstract Group<T> exclude(Class<?>... componentTypes);

    @Override
    public abstract Iterator<T> iterator();
    
    public abstract Stream<T> stream();

    public record With1<T>(T component, Entity entity) { }

    public record With2<T1, T2>(T1 component1, T2 component2, Entity entity) { }

    public record With3<T1, T2, T3>(T1 component1, T2 component2, T3 component3, Entity entity) { }

    public record With4<T1, T2, T3, T4>(T1 component1, T2 component2, T3 component3, T4 component4, Entity entity) { }

    public record With5<T1, T2, T3, T4, T5>(T1 component1, T2 component2, T3 component3, T4 component4, T5 component5, Entity entity) { }

    public record With6<T1, T2, T3, T4, T5, T6>(T1 component1, T2 component2, T3 component3, T4 component4, T5 component5, T6 component6, Entity entity) { }
}
