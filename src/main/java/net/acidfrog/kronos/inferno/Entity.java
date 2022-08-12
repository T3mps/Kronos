package net.acidfrog.kronos.inferno;

import java.util.concurrent.locks.StampedLock;

import net.acidfrog.kronos.crates.pool.ChunkedPool;
import net.acidfrog.kronos.crates.Identifiable;

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
    private Entity prev = null;
    private Entity next = null;
    private volatile Data data;
    private volatile StampedLock lock;

    protected Entity(int id, Composition composition, Object... components) {
        this.id = id;
        this.data = new Data(composition, components, (Data) null);
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

    public boolean modify(Registry registry, Composition newDataComposition, Object[] newComponentArray) {
        createLock();
        long stamp = lock.writeLock();

        try {
            if (isDetachedID()) {
                return false;
            }

            registry.modifyComponents(this, newDataComposition, newComponentArray);
            return true;
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

        return components != null && (composition.isMultiComponent() ?
               (index = composition.getComponentIndex(component.getClass())) > -1 && components[index].equals(component) :
               components[0].equals(component)
        );
    }

    protected boolean delete() {
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

    public Identifiable getPrevious() {
        return prev;
    }

    public Identifiable setPrevious(Identifiable prev) {
        var old = this.prev;
        this.prev = (Entity) prev;
        return old;
    }

    public Identifiable getNext() {
        return next;
    }

    public Identifiable setNext(Identifiable next) {
        var old = this.next;
        this.next = (Entity) next;
        return old;
    }

    public <S extends Enum<S>> Entity setState(S state) {
        return data.composition.setEntityState(this, state);
    }

    public Composition getComposition() {
        return data.composition;
    }

    public Object[] getComponents() {
        return data.components;
    }

    public Data getData() {
        return data;
    }

    Entity setData(Data data) {
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
        sb.append(", stateRootKey=").append(data.stateRoot);
        if (prev != null) {
            sb.append(", prev.id=").append(idSchema.idToString(prev.id));
        }
        if (next != null) {
            sb.append(", next.id=").append(idSchema.idToString(next.id));
        }
        sb.append('}');
        return sb.toString();
    }

    public record Data(Composition composition, Object[] components, IndexKey stateRoot) {

        public Data(Composition composition, Object[] components, Data other) {
            this(composition, components, other == null ? null : other.stateRoot);
        }
    }
}
