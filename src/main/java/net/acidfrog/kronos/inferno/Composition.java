package net.acidfrog.kronos.inferno;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class Composition {

    private final Registry registry;

    public Composition(Registry registry) {
        this.registry = registry;
    }

    private static final void populateIndices(final Class<?>[] componentTypes, final int[] indices, final DataComposition data) {
        if (data.isMultiComponent()) {
            for (int i = 0; i < componentTypes.length; i++) {
                indices[i] = data.fetchComponentIndex(componentTypes[i]);
            }
            
            return;
        }

        Class<?>[] newComponentTypes = data.getComponentTypes();
        Class<?> componentType = newComponentTypes.length > 0 ? newComponentTypes[0] : null;

        for (int i = 0; i < componentTypes.length; i++) {
            indices[i] = componentTypes[i].equals(componentType) ? 0 : -1;
        }
    }

    public <T> Transmute.Of1<T> of(Class<T> componentType) {
        return new Of1<T>(registry.getOrCreateByType(new Class<?>[]{componentType}));
    }

    public <T1, T2> Of2<T1, T2> of(Class<T1> componentType1, Class<T2> componentType2) {
        Class<?>[] componentTypes = { componentType1, componentType2 };
        return new Of2<T1, T2>(registry.getOrCreateByType(componentTypes), componentTypes);
    }

    public <T1, T2, T3> Of3<T1, T2, T3> of(Class<T1> componentType1, Class<T2> componentType2, Class<T3> componentType3) {
        Class<?>[] componentTypes = { componentType1, componentType2, componentType3 };
        return new Of3<T1, T2, T3>(registry.getOrCreateByType(componentTypes), componentTypes);
    }

    public <T1, T2, T3, T4> Of4<T1, T2, T3, T4> of(Class<T1> componentType1, Class<T2> componentType2, Class<T3> componentType3, Class<T4> componentType4) {
        Class<?>[] componentTypes = { componentType1, componentType2, componentType3, componentType4 };
        return new Of4<T1, T2, T3, T4>(registry.getOrCreateByType(componentTypes), componentTypes);
    }

    public <T1, T2, T3, T4, T5> Of5<T1, T2, T3, T4, T5> of(Class<T1> componentType1, Class<T2> componentType2, Class<T3> componentType3, Class<T4> componentType4, Class<T5> componentType5) {
        Class<?>[] componentTypes = { componentType1, componentType2, componentType3, componentType4, componentType5 };
        return new Of5<T1, T2, T3, T4, T5>(registry.getOrCreateByType(componentTypes), componentTypes);
    }

    public <T1, T2, T3, T4, T5, T6> Of6<T1, T2, T3, T4, T5, T6> of(Class<T1> componentType1, Class<T2> componentType2, Class<T3> componentType3, Class<T4> componentType4, Class<T5> componentType5, Class<T6> componentType6) {
        Class<?>[] componentTypes = { componentType1, componentType2, componentType3, componentType4, componentType5, componentType6 };
        return new Of6<T1, T2, T3, T4, T5, T6>(registry.getOrCreateByType(componentTypes), componentTypes);
    }

    public <T1, T2, T3, T4, T5, T6, T7> Of7<T1, T2, T3, T4, T5, T6, T7> of(Class<T1> componentType1, Class<T2> componentType2, Class<T3> componentType3, Class<T4> componentType4, Class<T5> componentType5, Class<T6> componentType6, Class<T7> componentType7) {
        Class<?>[] componentTypes = { componentType1, componentType2, componentType3, componentType4, componentType5, componentType6, componentType7 };
        return new Of7<T1, T2, T3, T4, T5, T6, T7>(registry.getOrCreateByType(componentTypes), componentTypes);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8> Of8<T1, T2, T3, T4, T5, T6, T7, T8> of(Class<T1> componentType1, Class<T2> componentType2, Class<T3> componentType3, Class<T4> componentType4, Class<T5> componentType5, Class<T6> componentType6, Class<T7> componentType7, Class<T8> componentType8) {
        Class<?>[] componentTypes = { componentType1, componentType2, componentType3, componentType4, componentType5, componentType6, componentType7, componentType8 };
        return new Of8<T1, T2, T3, T4, T5, T6, T7, T8>(registry.getOrCreateByType(componentTypes), componentTypes);
    }

    public Transmute.ByRemoving byRemoving(Class<?>... removedComponentTypes) {
        return new PreparedModifier(registry, null, removedComponentTypes);
    }

    public <T> ByAdding1AndRemoving<T> byAdding1AndRemoving(Class<T> componentType, Class<?>... removedComponentTypes) {
        return new ByAdding1AndRemoving<>(registry, new Class<?>[]{componentType}, removedComponentTypes);
    }

    public <T1, T2> ByAdding2AndRemoving<T1, T2> byAdding2AndRemoving(Class<T1> componentType1, Class<T2> componentType2, Class<?>... removedComponentTypes) {
        return new ByAdding2AndRemoving<>(registry, new Class<?>[]{componentType1, componentType2}, removedComponentTypes);
    }

    public <T1, T2, T3> ByAdding3AndRemoving<T1, T2, T3> byAdding3AndRemoving(Class<T1> componentType1, Class<T2> componentType2, Class<T3> componentType3, Class<?>... removedComponentTypes) {
        return new ByAdding3AndRemoving<>(registry, new Class<?>[]{componentType1, componentType2, componentType3}, removedComponentTypes);
    }

    public <T1, T2, T3, T4> ByAdding4AndRemoving<T1, T2, T3, T4> byAdding4AndRemoving(Class<T1> componentType1, Class<T2> componentType2, Class<T3> componentType3, Class<T4> componentType4, Class<?>... removedComponentTypes) {
        return new ByAdding4AndRemoving<>(registry, new Class<?>[]{componentType1, componentType2, componentType3, componentType4}, removedComponentTypes);
    }

    public <T1, T2, T3, T4, T5> ByAdding5AndRemoving<T1, T2, T3, T4, T5> byAdding5AndRemoving(Class<T1> componentType1, Class<T2> componentType2, Class<T3> componentType3, Class<T4> componentType4, Class<T5> componentType5, Class<?>... removedComponentTypes) {
        return new ByAdding5AndRemoving<>(registry, new Class<?>[]{componentType1, componentType2, componentType3, componentType4, componentType5}, removedComponentTypes);
    }

    public <T1, T2, T3, T4, T5, T6> ByAdding6AndRemoving<T1, T2, T3, T4, T5, T6> byAdding6AndRemoving(Class<T1> componentType1, Class<T2> componentType2, Class<T3> componentType3, Class<T4> componentType4, Class<T5> componentType5, Class<T6> componentType6, Class<?>... removedComponentTypes) {
        return new ByAdding6AndRemoving<>(registry, new Class<?>[]{componentType1, componentType2, componentType3, componentType4, componentType5, componentType6}, removedComponentTypes);
    }

    public <T1, T2, T3, T4, T5, T6, T7> ByAdding7AndRemoving<T1, T2, T3, T4, T5, T6, T7> byAdding7AndRemoving(Class<T1> componentType1, Class<T2> componentType2, Class<T3> componentType3, Class<T4> componentType4, Class<T5> componentType5, Class<T6> componentType6, Class<T7> componentType7, Class<?>... removedComponentTypes) {
        return new ByAdding7AndRemoving<>(registry, new Class<?>[]{componentType1, componentType2, componentType3, componentType4, componentType5, componentType6, componentType7}, removedComponentTypes);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8> ByAdding8AndRemoving<T1, T2, T3, T4, T5, T6, T7, T8> byAdding8AndRemoving(Class<T1> componentType1, Class<T2> componentType2, Class<T3> componentType3, Class<T4> componentType4, Class<T5> componentType5, Class<T6> componentType6, Class<T7> componentType7, Class<T8> componentType8, Class<?>... removedComponentTypes) {
        return new ByAdding8AndRemoving<>(registry, new Class<?>[]{componentType1, componentType2, componentType3, componentType4, componentType5, componentType6, componentType7, componentType8}, removedComponentTypes);
    }

    private static class OfTypes {

        protected final DataComposition data;
        protected final int[] indices;

        protected Object[] components;

        public OfTypes(DataComposition data, Class<?>[] componentTypes) {
            this.data = data;

            if (componentTypes == null) {
                this.indices = new int[0];
            } else {
                int length = componentTypes.length;
                this.indices = new int[length];
                populateIndices(componentTypes, indices, data);
            }
        }

        public Object[] getComponents() {
            return components;
        }

        public Object getContext() {
            return data;
        }
    }

    public record NewEntityComposition(IntEntity entity, DataComposition dataComposition, Object[] components) {
    }
    
    private static class PreparedModifier implements Transmute.ByRemoving {

        protected final Registry registry;
        protected final Map<DataComposition, TargetComposition> cache;
        protected final Class<?>[] addedComponentTypes;
        protected final Set<Class<?>> removedComponentTypes;
        protected NewEntityComposition modifier;

        public PreparedModifier(Registry registry, Class<?>[] addedComponentTypes, Class<?>... removedComponentTypes) {
            this.registry = registry;
            this.cache = new ConcurrentHashMap<DataComposition, TargetComposition>();;
            this.addedComponentTypes = addedComponentTypes;
            this.removedComponentTypes = new HashSet<Class<?>>(removedComponentTypes.length);

            Collections.addAll(this.removedComponentTypes, removedComponentTypes);
        }

        @Override
        public Transmute.Modifier withValue(Entity entity) {
            modifier = fetchModifier(entity);
            return this;
        }

        protected NewEntityComposition fetchModifier(Entity entity, Object... addedComponents) {
            var intEntity = (IntEntity) entity;
            var composition = intEntity.getComposition();
            var targetComposition = fetchTargetComposition(composition);
            return !targetComposition.target.equals(composition) ? new NewEntityComposition(intEntity, targetComposition.target, fetchComponentArray(intEntity, targetComposition, addedComponents)) : null;
        }

        private Object[] fetchComponentArray(IntEntity entity, TargetComposition targetComposition, Object... addedComponents) {
            int tSize = targetComposition.target.getComponentTypes().length;
            if (tSize == 0) {
                return new Object[0];
            }

            Object[] components = new Object[tSize];
            Object[] prevComponentArray = entity.getComponents();

            if (prevComponentArray != null && prevComponentArray.length > 0) {
                populateComponentArray(components, prevComponentArray, targetComposition.indices);
            }
            if (addedComponents.length > 0) {
                populateComponentArray(components, addedComponents, targetComposition.addedIndices);
            }
            
            return components;
        }

        private void populateComponentArray(Object[] components, Object[] otherComponents, int[] indices) {
            int oSize = otherComponents.length;

            for (int i = 0; i < oSize; i++) {
                int index = indices[i];

                if (index < 0) {
                    continue;
                }
                components[index] = otherComponents[i];
            }
        }

        @Override
        public Object getModifier() {
            return modifier;
        }

        private TargetComposition fetchTargetComposition(DataComposition composition) {
            return cache.computeIfAbsent(composition, prevComposition -> {
                Class<?>[] prevComponentTypes = prevComposition.getComponentTypes();
                int newLength = prevComponentTypes.length + (addedComponentTypes == null ? 0 : addedComponentTypes.length);
                List<Class<?>> typeList = new ArrayList<Class<?>>(newLength);
                
                populateTypeList(typeList, prevComponentTypes);

                if (addedComponentTypes != null) {
                    populateTypeList(typeList, addedComponentTypes);
                }
                
                Class<?>[] newComponentTypes = typeList.toArray(new Class<?>[0]);
                DataComposition newComposition = registry.getOrCreateByType(newComponentTypes);
                int[] indices = new int[prevComponentTypes.length];
                
                populateIndices(prevComponentTypes, indices, newComposition);
                
                int[] addedIndices = null;
                if (addedComponentTypes != null) {
                    addedIndices = new int[addedComponentTypes.length];
                    populateIndices(addedComponentTypes, addedIndices, newComposition);
                }
                
                return new TargetComposition(newComposition, indices, addedIndices);
            });
        }

        private void populateTypeList(List<Class<?>> typeList, Class<?>[] componentTypes) {
            for (var type : componentTypes) {
                if (!removedComponentTypes.contains(type)) {
                    typeList.add(type);
                }
            }
        }

        private record TargetComposition(DataComposition target, int[] indices, int[] addedIndices) {
        }
    }

    public final static class Of1<T> extends OfTypes implements Transmute.Of1<T> {

        public Of1(DataComposition data) {
            super(data, null);
        }

        @Override
        public Transmute.OfTypes withValue(T comp) {
            components = new Object[]{ comp };
            return this;
        }
    }

    public final static class Of2<T1, T2> extends OfTypes implements Transmute.Of2<T1, T2> {

        public Of2(DataComposition data, Class<?>[] componentTypes) {
            super(data, componentTypes);
        }

        @Override
        public Transmute.OfTypes withValue(T1 component1, T2 component2) {
            components = new Object[2];
            components[indices[0]] = component1;
            components[indices[1]] = component2;
            return this;
        }
    }

    public final static class Of3<T1, T2, T3> extends OfTypes implements Transmute.Of3<T1, T2, T3> {

        public Of3(DataComposition data, Class<?>[] componentTypes) {
            super(data, componentTypes);
        }

        @Override
        public Transmute.OfTypes withValue(T1 component1, T2 component2, T3 component3) {
            components = new Object[3];
            components[indices[0]] = component1;
            components[indices[1]] = component2;
            components[indices[2]] = component3;
            return this;
        }
    }

    public final static class Of4<T1, T2, T3, T4> extends OfTypes implements Transmute.Of4<T1, T2, T3, T4> {

        public Of4(DataComposition data, Class<?>[] componentTypes) {
            super(data, componentTypes);
        }

        @Override
        public Transmute.OfTypes withValue(T1 component1, T2 component2, T3 component3, T4 component4) {
            components = new Object[4];
            components[indices[0]] = component1;
            components[indices[1]] = component2;
            components[indices[2]] = component3;
            components[indices[3]] = component4;
            return this;
        }
    }

    public final static class Of5<T1, T2, T3, T4, T5> extends OfTypes implements Transmute.Of5<T1, T2, T3, T4, T5> {

        public Of5(DataComposition data, Class<?>[] componentTypes) {
            super(data, componentTypes);
        }

        @Override
        public Transmute.OfTypes withValue(T1 component1, T2 component2, T3 component3, T4 component4, T5 component5) {
            components = new Object[5];
            components[indices[0]] = component1;
            components[indices[1]] = component2;
            components[indices[2]] = component3;
            components[indices[3]] = component4;
            components[indices[4]] = component5;
            return this;
        }
    }

    public final static class Of6<T1, T2, T3, T4, T5, T6> extends OfTypes implements Transmute.Of6<T1, T2, T3, T4, T5, T6> {

        public Of6(DataComposition data, Class<?>[] componentTypes) {
            super(data, componentTypes);
        }

        @Override
        public Transmute.OfTypes withValue(T1 component1, T2 component2, T3 component3, T4 component4, T5 component5, T6 component6) {
            components = new Object[6];
            components[indices[0]] = component1;
            components[indices[1]] = component2;
            components[indices[2]] = component3;
            components[indices[3]] = component4;
            components[indices[4]] = component5;
            components[indices[5]] = component6;
            return this;
        }
    }

    public final static class Of7<T1, T2, T3, T4, T5, T6, T7> extends OfTypes implements Transmute.Of7<T1, T2, T3, T4, T5, T6, T7> {

        public Of7(DataComposition data, Class<?>[] componentTypes) {
            super(data, componentTypes);
        }

        @Override
        public Transmute.OfTypes withValue(T1 component1, T2 component2, T3 component3, T4 component4, T5 component5, T6 component6, T7 component7) {
            components = new Object[7];
            components[indices[0]] = component1;
            components[indices[1]] = component2;
            components[indices[2]] = component3;
            components[indices[3]] = component4;
            components[indices[4]] = component5;
            components[indices[5]] = component6;
            components[indices[6]] = component7;
            return this;
        }
    }

    public final static class Of8<T1, T2, T3, T4, T5, T6, T7, T8> extends OfTypes implements Transmute.Of8<T1, T2, T3, T4, T5, T6, T7, T8> {

        public Of8(DataComposition data, Class<?>[] componentTypes) {
            super(data, componentTypes);
        }

        @Override
        public Transmute.OfTypes withValue(T1 component1, T2 component2, T3 component3, T4 component4, T5 component5, T6 component6, T7 component7, T8 component8) {
            components = new Object[8];
            components[indices[0]] = component1;
            components[indices[1]] = component2;
            components[indices[2]] = component3;
            components[indices[3]] = component4;
            components[indices[4]] = component5;
            components[indices[5]] = component6;
            components[indices[6]] = component7;
            components[indices[7]] = component8;
            return this;
        }
    }

    public final static class ByAdding1AndRemoving<T> extends PreparedModifier implements Transmute.ByAdding1AndRemoving<T> {

        public ByAdding1AndRemoving(Registry registry, Class<?>[] addedComponentTypes, Class<?>... componentTypes) {
            super(registry, addedComponentTypes, componentTypes);
        }

        @Override
        public Transmute.Modifier withValue(Entity entity, T comp) {
            modifier = fetchModifier(entity, comp);
            return this;
        }
    }

    public final static class ByAdding2AndRemoving<T1, T2> extends PreparedModifier implements Transmute.ByAdding2AndRemoving<T1, T2> {

        public ByAdding2AndRemoving(Registry registry, Class<?>[] addedComponentTypes, Class<?>... componentTypes) {
            super(registry, addedComponentTypes, componentTypes);
        }

        @Override
        public Transmute.Modifier withValue(Entity entity, T1 component1, T2 component2) {
            modifier = fetchModifier(entity, component1, component2);
            return this;
        }
    }

    public final static class ByAdding3AndRemoving<T1, T2, T3> extends PreparedModifier implements Transmute.ByAdding3AndRemoving<T1, T2, T3> {

        public ByAdding3AndRemoving(Registry registry, Class<?>[] addedComponentTypes, Class<?>... componentTypes) {
            super(registry, addedComponentTypes, componentTypes);
        }

        @Override
        public Transmute.Modifier withValue(Entity entity, T1 component1, T2 component2, T3 component3) {
            modifier = fetchModifier(entity, component1, component2, component3);
            return this;
        }
    }

    public final static class ByAdding4AndRemoving<T1, T2, T3, T4> extends PreparedModifier implements Transmute.ByAdding4AndRemoving<T1, T2, T3, T4> {

        public ByAdding4AndRemoving(Registry registry, Class<?>[] addedComponentTypes, Class<?>... componentTypes) {
            super(registry, addedComponentTypes, componentTypes);
        }

        @Override
        public Transmute.Modifier withValue(Entity entity, T1 component1, T2 component2, T3 component3, T4 component4) {
            modifier = fetchModifier(entity, component1, component2, component3, component4);
            return this;
        }
    }

    public final static class ByAdding5AndRemoving<T1, T2, T3, T4, T5> extends PreparedModifier implements Transmute.ByAdding5AndRemoving<T1, T2, T3, T4, T5> {

        public ByAdding5AndRemoving(Registry registry, Class<?>[] addedComponentTypes, Class<?>... componentTypes) {
            super(registry, addedComponentTypes, componentTypes);
        }

        @Override
        public Transmute.Modifier withValue(Entity entity, T1 component1, T2 component2, T3 component3, T4 component4, T5 component5) {
            modifier = fetchModifier(entity, component1, component2, component3, component4, component5);
            return this;
        }
    }

    public final static class ByAdding6AndRemoving<T1, T2, T3, T4, T5, T6> extends PreparedModifier implements Transmute.ByAdding6AndRemoving<T1, T2, T3, T4, T5, T6> {

        public ByAdding6AndRemoving(Registry registry, Class<?>[] addedComponentTypes, Class<?>... componentTypes) {
            super(registry, addedComponentTypes, componentTypes);
        }

        @Override
        public Transmute.Modifier withValue(Entity entity, T1 component1, T2 component2, T3 component3, T4 component4, T5 component5, T6 component6) {
            modifier = fetchModifier(entity, component1, component2, component3, component4, component5, component6);
            return this;
        }
    }

    public final static class ByAdding7AndRemoving<T1, T2, T3, T4, T5, T6, T7> extends PreparedModifier implements Transmute.ByAdding7AndRemoving<T1, T2, T3, T4, T5, T6, T7> {

        public ByAdding7AndRemoving(Registry registry, Class<?>[] addedComponentTypes, Class<?>... componentTypes) {
            super(registry, addedComponentTypes, componentTypes);
        }

        @Override
        public Transmute.ByRemoving withValue(Entity entity, T1 component1, T2 component2, T3 component3, T4 component4, T5 component5, T6 component6, T7 component7) {
            modifier = fetchModifier(entity, component1, component2, component3, component4, component5, component6, component7);
            return this;
        }
    }

    public final static class ByAdding8AndRemoving<T1, T2, T3, T4, T5, T6, T7, T8> extends PreparedModifier implements Transmute.ByAdding8AndRemoving<T1, T2, T3, T4, T5, T6, T7, T8> {

        public ByAdding8AndRemoving(Registry registry, Class<?>[] addedComponentTypes, Class<?>... componentTypes) {
            super(registry, addedComponentTypes, componentTypes);
        }

        @Override
        public Transmute.ByRemoving withValue(Entity entity, T1 component1, T2 component2, T3 component3, T4 component4, T5 component5, T6 component6, T7 component7, T8 component8) {
            modifier = fetchModifier(entity, component1, component2, component3, component4, component5, component6, component7, component8);
            return this;
        }
    }
}
