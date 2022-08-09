package net.acidfrog.kronos.inferno;

import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.acidfrog.kronos.inferno.core.IndexKey;

public abstract class View<T> implements Group<T> {
    
    private final CompositionRepository compositionRepository;
    private final Map<IndexKey, CompositionRepository.Node> nodeMap;
    protected IndexKey stateKey;

    protected View(CompositionRepository compositionRepository, Map<IndexKey, CompositionRepository.Node> nodeMap) {
        this.compositionRepository = compositionRepository;
        this.nodeMap = nodeMap;
    }

    protected abstract Iterator<T> compositionIterator(DataComposition composition);

    @Override
    public View<T> include(Class<?>... componentTypes) {
        compositionRepository.include(nodeMap, componentTypes);
        return this;
    }

    @Override
    public View<T> exclude(Class<?>... componentTypes) {
        compositionRepository.exclude(nodeMap, componentTypes);
        return this;
    }

    @Override
    public <S extends Enum<S>> View<T> withState(S state) {
        stateKey = DataComposition.computeIndexKey(state, compositionRepository.getClassIndex());
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

    private static final class IteratorWrapper<T> implements Iterator<T> {
        
        private final View<T> owner;
        private final Iterator<CompositionRepository.Node> nodesIterator;
        private Iterator<T> wrapped;

        public IteratorWrapper(View<T> owner, Iterator<CompositionRepository.Node> nodesIterator) {
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
        
        protected With1(CompositionRepository compositionRepository, Map<IndexKey, CompositionRepository.Node> nodeMap, Class<T> type) {
            super(compositionRepository, nodeMap);
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

        protected With2(CompositionRepository compositionRepository, Map<IndexKey, CompositionRepository.Node> nodeMap, Class<T1> type1, Class<T2> type2) {
            super(compositionRepository, nodeMap);
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

        protected With3(CompositionRepository compositionRepository, Map<IndexKey, CompositionRepository.Node> nodeMap, Class<T1> type1, Class<T2> type2, Class<T3> type3) {
            super(compositionRepository, nodeMap);
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

        protected With4(CompositionRepository compositionRepository, Map<IndexKey, CompositionRepository.Node> nodeMap, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4) {
            super(compositionRepository, nodeMap);
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

        protected With5(CompositionRepository compositionRepository, Map<IndexKey, CompositionRepository.Node> nodeMap, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5) {
            super(compositionRepository, nodeMap);
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

        protected With6(CompositionRepository compositionRepository, Map<IndexKey, CompositionRepository.Node> nodeMap, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6) {
            super(compositionRepository, nodeMap);
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
