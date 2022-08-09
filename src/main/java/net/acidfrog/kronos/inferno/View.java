package net.acidfrog.kronos.inferno;

import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.acidfrog.kronos.inferno.core.IndexKey;

public abstract class View<T> implements Group<T> {
    
    private final Registry registry;
    private final Map<IndexKey, Registry.Node> nodeMap;
    protected IndexKey stateKey;

    protected View(Registry registry, Map<IndexKey, Registry.Node> nodeMap) {
        this.registry = registry;
        this.nodeMap = nodeMap;
    }

    protected abstract Iterator<T> compositionIterator(DataComposition composition);

    @Override
    public View<T> include(Class<?>... componentTypes) {
        registry.include(nodeMap, componentTypes);
        return this;
    }

    @Override
    public View<T> exclude(Class<?>... componentTypes) {
        registry.exclude(nodeMap, componentTypes);
        return this;
    }

    @Override
    public <S extends Enum<S>> View<T> withState(S state) {
        stateKey = DataComposition.computeIndexKey(state, registry.getClassIndex());
        return this;
    }

    @Override
    public Iterator<T> iterator() {
        return nodeMap != null ? (nodeMap.size() > 1 ? new IteratorWrapper<T>(this, nodeMap.values().iterator()) :
                                  compositionIterator(nodeMap.values().iterator().next().getComposition())) :
                                  new Iterator<T>() {
                                      @Override
                                      public boolean hasNext() {
                                          return false;
                                      }

                                      @Override
                                      public T next() {
                                          return null;
                                      }
                                  };
    }

    @Override
    public Stream<T> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), false);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("View { ");
        nodeMap.forEach((key, node) -> sb.append(node.getComposition()).append(", "));
        int sSize = sb.length();
        if (sSize > 2) {
            sb.setLength(--sSize);
            sb.setCharAt(sSize - 1, '\s');
        }
        sb.append("}");
        return sb.toString();
    }

    private static final class IteratorWrapper<T> implements Iterator<T> {
        
        private final View<T> owner;
        private final Iterator<Registry.Node> nodesIterator;
        private Iterator<T> wrapped;

        public IteratorWrapper(View<T> owner, Iterator<Registry.Node> nodesIterator) {
            this.owner = owner;
            this.nodesIterator = nodesIterator;
            this.wrapped = this.nodesIterator.hasNext() ?
                           owner.compositionIterator(this.nodesIterator.next().getComposition()) :
                           new Iterator<T>() {
                               @Override
                               public boolean hasNext() {
                                   return false;
                               }

                               @Override
                               public T next() {
                                   return null;
                               }
                            };
        }

        @Override
        public boolean hasNext() {
            return wrapped.hasNext() || (nodesIterator.hasNext() && (wrapped = owner.compositionIterator(nodesIterator.next().getComposition())).hasNext());
        }

        @Override
        public T next() {
            return wrapped.next();
        }
    }

    public final static class With1<T> extends View<Group.With1<T>> {

        private final Class<T> type;
        
        protected With1(Registry registry, Map<IndexKey, Registry.Node> nodeMap, Class<T> type) {
            super(registry, nodeMap);
            this.type = type;
        }

        @Override
        protected Iterator<Group.With1<T>> compositionIterator(DataComposition composition) {
            Iterator<IntEntity> iterator = stateKey == null ? composition.getTenant().iterator() : composition.entityStateIterator(composition.getStateRootEntity(stateKey));
            return composition.select(type, iterator);
        }
    }

    public final static class With2<T1, T2> extends View<Group.With2<T1, T2>> {
        
        private final Class<T1> type1;
        private final Class<T2> type2;

        protected With2(Registry registry, Map<IndexKey, Registry.Node> nodeMap, Class<T1> type1, Class<T2> type2) {
            super(registry, nodeMap);
            this.type1 = type1;
            this.type2 = type2;
        }

        @Override
        protected Iterator<Group.With2<T1, T2>> compositionIterator(DataComposition composition) {
            Iterator<IntEntity> iterator = stateKey == null ? composition.getTenant().iterator() : composition.entityStateIterator(composition.getStateRootEntity(stateKey));
            return composition.select(type1, type2, iterator);
        }
    }

    public final static class With3<T1, T2, T3> extends View<Group.With3<T1, T2, T3>> {

        private final Class<T1> type1;
        private final Class<T2> type2;
        private final Class<T3> type3;

        protected With3(Registry registry, Map<IndexKey, Registry.Node> nodeMap, Class<T1> type1, Class<T2> type2, Class<T3> type3) {
            super(registry, nodeMap);
            this.type1 = type1;
            this.type2 = type2;
            this.type3 = type3;
        }

        @Override
        protected Iterator<Group.With3<T1, T2, T3>> compositionIterator(DataComposition composition) {
            Iterator<IntEntity> iterator = stateKey == null ? composition.getTenant().iterator() : composition.entityStateIterator(composition.getStateRootEntity(stateKey));
            return composition.select(type1, type2, type3, iterator);
        }
    }

    public final static class With4<T1, T2, T3, T4> extends View<Group.With4<T1, T2, T3, T4>> {

        private final Class<T1> type1;
        private final Class<T2> type2;
        private final Class<T3> type3;
        private final Class<T4> type4;

        protected With4(Registry registry, Map<IndexKey, Registry.Node> nodeMap, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4) {
            super(registry, nodeMap);
            this.type1 = type1;
            this.type2 = type2;
            this.type3 = type3;
            this.type4 = type4;
        }

        @Override
        protected Iterator<Group.With4<T1, T2, T3, T4>> compositionIterator(DataComposition composition) {
            Iterator<IntEntity> iterator = stateKey == null ? composition.getTenant().iterator() : composition.entityStateIterator(composition.getStateRootEntity(stateKey));
            return composition.select(type1, type2, type3, type4, iterator);
        }
    }

    public final static class With5<T1, T2, T3, T4, T5> extends View<Group.With5<T1, T2, T3, T4, T5>> {
        
        private final Class<T1> type1;
        private final Class<T2> type2;
        private final Class<T3> type3;
        private final Class<T4> type4;
        private final Class<T5> type5;

        protected With5(Registry registry, Map<IndexKey, Registry.Node> nodeMap, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5) {
            super(registry, nodeMap);
            this.type1 = type1;
            this.type2 = type2;
            this.type3 = type3;
            this.type4 = type4;
            this.type5 = type5;
        }

        @Override
        protected Iterator<Group.With5<T1, T2, T3, T4, T5>> compositionIterator(DataComposition composition) {
            Iterator<IntEntity> iterator = stateKey == null ? composition.getTenant().iterator() : composition.entityStateIterator(composition.getStateRootEntity(stateKey));
            return composition.select(type1, type2, type3, type4, type5, iterator);
        }
    }

    public final static class With6<T1, T2, T3, T4, T5, T6> extends View<Group.With6<T1, T2, T3, T4, T5, T6>> {

        private final Class<T1> type1;
        private final Class<T2> type2;
        private final Class<T3> type3;
        private final Class<T4> type4;
        private final Class<T5> type5;
        private final Class<T6> type6;

        protected With6(Registry registry, Map<IndexKey, Registry.Node> nodeMap, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6) {
            super(registry, nodeMap);
            this.type1 = type1;
            this.type2 = type2;
            this.type3 = type3;
            this.type4 = type4;
            this.type5 = type5;
            this.type6 = type6;
        }

        @Override
        protected Iterator<Group.With6<T1, T2, T3, T4, T5, T6>> compositionIterator(DataComposition composition) {
            Iterator<IntEntity> iterator = stateKey == null ? composition.getTenant().iterator() : composition.entityStateIterator(composition.getStateRootEntity(stateKey));
            return composition.select(type1, type2, type3, type4, type5, type6, iterator);
        }
    }
}
