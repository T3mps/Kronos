package com.starworks.kronos.inferno;

import java.util.concurrent.locks.StampedLock;

import com.starworks.kronos.structures.Identifiable;
import com.starworks.kronos.structures.pool.ChunkedPool;
import com.starworks.kronos.toolkit.internal.memory.UncheckedReferenceUpdater;

public final class Entity implements Identifiable {
    
    private static final UncheckedReferenceUpdater<Entity, StampedLock> lockUpdater;

    static {
        UncheckedReferenceUpdater<Entity, StampedLock> updater = null;

        try {
            updater = new UncheckedReferenceUpdater<Entity, StampedLock>(Entity.class, "lock");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        lockUpdater = updater;
    }

    private int id;
    private volatile ComponentData data;
    private volatile StampedLock lock;

    protected Entity(int id, Composition composition, Object... components) {
        this.id = id;
        this.data = new ComponentData(composition, components);
    }

    public Entity add(Object component) {
        createLock();
        
        long stamp = lock.writeLock();

        try {
            if (isDetachedID()) {
                return null;
            }
            
            return data.composition.getRepository().addComponent(this, component);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public boolean remove(Object component) {
        createLock();
        long stamp = lock.writeLock();
        
        try {
            if (isDetachedID()) {
                return false;
            }

            return data.composition.getRepository().removeComponentType(this, component.getClass());
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public boolean removeType(Class<?> componentType) {
        createLock();
        long stamp = lock.writeLock();
        
        try {
            if (isDetachedID()) {
                return false;
            }

            return data.composition.getRepository().removeComponentType(this, componentType);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public boolean has(Class<?> componentType) {
        var components = data.components;
        var composition = data.composition;
        return components != null && (composition.isMultiComponent() ? composition.getComponentIndex(componentType) > -1 : components[0].getClass().equals(componentType));
    }

    public boolean contains(Object component) {
        int index;
        var components = data.components;
        var composition = data.composition;

        return components != null && (composition.isMultiComponent() ? (index = composition.getComponentIndex(component.getClass())) > -1 && components[index].equals(component) : components[0].equals(component));
    }

    public Object get(Class<?> componentType) {
        var components = data.components;
        if (components == null) {
            return null;
        }

        for (var component : components) {
            if (componentType.isInstance(component)) {
                return component;
            }
        }

        return null;
    }

    protected boolean destroy() {
        createLock();
        long stamp = lock.writeLock();
        
        try {
            if (isDetachedID()) {
                return false;
            }

            return data.composition.deleteEntity(this);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    private void createLock() {
        if (lock != null) {
            return;

        }
        lockUpdater.compareAndSet(this, null, new StampedLock());
    }

    public int getID() {
        return id;
    }

    public int setID(int id) {
        return this.id = id | (this.id & ChunkedPool.IDSchema.FLAG_BIT);
    }

    public Composition getComposition() {
        return data.composition;
    }

    public Object[] getComponents() {
        return data.components;
    }

    public ComponentData getData() {
        return data;
    }

    Entity setData(ComponentData data) {
        this.data = data;
        return this;
    }

    public boolean isEnabled() {
        return !isDetachedID();
    }

    public Entity setEnabled(boolean enabled) {
        createLock();
        long stamp = lock.writeLock();
        try {
            if (enabled && isDetachedID()) {
                data.composition.reattachEntity(this);
            } else if (!enabled && isEnabled()) {
                data.composition.detachEntity(this);
            }
            return this;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public boolean isPooledArray() {
        return (id & ChunkedPool.IDSchema.FLAG_BIT) == ChunkedPool.IDSchema.FLAG_BIT;
    }

    protected void flagPooledArray() {
        id |= ChunkedPool.IDSchema.FLAG_BIT;
    }

    protected boolean isDetachedID() {
        return (id & ChunkedPool.IDSchema.DETACHED_BIT) == ChunkedPool.IDSchema.DETACHED_BIT;
    }

    protected void flagDetachedID() {
        id |= ChunkedPool.IDSchema.DETACHED_BIT;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        var idSchema = data.composition.getIDSchema();
        sb.append("Entity={");
        sb.append("id=").append(idSchema.idToString(id));
        sb.append(", ").append(data.composition);
        sb.append('}');
        return sb.toString();
    }

    public record ComponentData(Composition composition, Object[] components) {
    }
}
