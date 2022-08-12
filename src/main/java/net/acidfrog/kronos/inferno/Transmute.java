package net.acidfrog.kronos.inferno;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

final class Transmute {

    private Transmute() {}

    private static final void populateIndices(final Class<?>[] componentTypes, final int[] indices, final Composition data) {
        if (data.isMultiComponent()) {
            for (int i = 0; i < componentTypes.length; i++) {
                indices[i] = data.getComponentIndex(componentTypes[i]);
            }
            
            return;
        }

        Class<?>[] newComponentTypes = data.getComponentTypes();
        Class<?> componentType = newComponentTypes.length > 0 ? newComponentTypes[0] : null;

        for (int i = 0; i < componentTypes.length; i++) {
            indices[i] = componentTypes[i].equals(componentType) ? 0 : -1;
        }
    }

    public interface OfTypes {

        public abstract Object[] getComponents();

        public abstract Object getContext();
    }

    protected static non-sealed class PreparedModifier implements ByRemoving {

        protected final Registry registry;
        protected final Map<Composition, TargetComposition> cache;
        protected final Class<?>[] addedComponentTypes;
        protected final Set<Class<?>> removedComponentTypes;
        protected NewEntityComposition modifier;

        protected PreparedModifier(Registry registry, Class<?>[] addedComponentTypes, Class<?>... removedComponentTypes) {
            this.registry = registry;
            this.cache = new ConcurrentHashMap<Composition, TargetComposition>();;
            this.addedComponentTypes = addedComponentTypes;
            this.removedComponentTypes = new HashSet<Class<?>>(removedComponentTypes.length);

            Collections.addAll(this.removedComponentTypes, removedComponentTypes);
        }
        
        @Override
        public Transmute.Modifier withValue(Entity entity) {
            modifier = getModifier(entity);
            return this;
        }

        protected NewEntityComposition getModifier(Entity entity, Object... addedComponents) {
            var intEntity = (Entity) entity;
            var composition = intEntity.getComposition();
            var targetComposition = getTargetComposition(composition);
            return !targetComposition.target.equals(composition) ? new NewEntityComposition(intEntity, targetComposition.target, getComponentArray(intEntity, targetComposition, addedComponents)) : null;
        }

        private Object[] getComponentArray(Entity entity, TargetComposition targetComposition, Object... addedComponents) {
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

        private TargetComposition getTargetComposition(Composition composition) {
            return cache.computeIfAbsent(composition, prevComposition -> {
                Class<?>[] prevComponentTypes = prevComposition.getComponentTypes();
                int newLength = prevComponentTypes.length + (addedComponentTypes == null ? 0 : addedComponentTypes.length);
                List<Class<?>> typeList = new ArrayList<Class<?>>(newLength);
                
                populateTypeList(typeList, prevComponentTypes);

                if (addedComponentTypes != null) {
                    populateTypeList(typeList, addedComponentTypes);
                }
                
                Class<?>[] newComponentTypes = typeList.toArray(new Class<?>[0]);
                Composition newComposition = registry.getOrCreateByType(newComponentTypes);
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
    }
    
    private record TargetComposition(Composition target, int[] indices, int[] addedIndices) {
    }

    protected record NewEntityComposition(Entity entity, Composition dataComposition, Object[] components) {
    }

    public sealed interface Modifier permits ByRemoving {

        public abstract Object getModifier();
    }

    public sealed interface ByRemoving extends Modifier permits PreparedModifier {

        public abstract Object getModifier();

        public abstract Modifier withValue(Entity entity);
    }

    public final static class ByAddingAndRemoving<T> extends PreparedModifier {

        public ByAddingAndRemoving(Registry registry, Class<?>[] addedComponentTypes, Class<?>... componentTypes) {
            super(registry, addedComponentTypes, componentTypes);
        }

        public Transmute.Modifier withValue(Entity entity, T comp) {
            modifier = getModifier(entity, comp);
            return this;
        }
    }

}
