package net.acidfrog.kronos.scene.ecs;

import java.util.HashMap;
import java.util.Map;

import net.acidfrog.kronos.core.datastructure.Bitset;
import net.acidfrog.kronos.scene.ecs.component.Component;

public class ComponentType {

    private static Map<Class<? extends Component>, ComponentType> componentTypes = new HashMap<Class<? extends Component>, ComponentType>();
    private static int pointer = 0;

    private final int index;

    private ComponentType() {
        this.index = pointer++;
    }

    public static ComponentType getFor(Class<? extends Component> componentType) {
        ComponentType type = componentTypes.get(componentType);

        if (type == null) {
            type = new ComponentType();
            componentTypes.put(componentType, type);
        }
        
        return type;
    }

    public static int getIndexFor(Class<? extends Component> componentType) {
        return getFor(componentType).getIndex();
    }

    @SafeVarargs
    public static Bitset getBitsFor(Class<? extends Component>... componentTypes) {
        Bitset bitset = new Bitset();
		int typesLength = componentTypes.length;

		for (int i = 0; i < typesLength; i++) bitset.set(ComponentType.getIndexFor(componentTypes[i]));

		return bitset;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public int hashCode() {
        return index;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof ComponentType)) return false;

        return ((ComponentType) obj).index == index;
    }

}
