package net.acidfrog.kronos.scene.ecs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.acidfrog.kronos.core.lang.error.KronosError;
import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;
import net.acidfrog.kronos.core.util.UUID;

public class Engine {

    private List<Entity> entities = new ArrayList<Entity>();
    private Map<UUID, Entity> entitiesById = new HashMap<UUID, Entity>();
    private Map<Family, List<Entity>> views = new HashMap<Family, List<Entity>>();
    private List<Command> commands = new ArrayList<Command>();
    private List<EngineSystem> systems = new ArrayList<EngineSystem>();
    private List<EntityListener> listeners = new ArrayList<EntityListener>();
    private Map<Family, List<EntityListener>> filteredListeners = new HashMap<Family, List<EntityListener>>();
    private boolean updating = false;
    
    public void update(float dt) {
        if (updating) return;
        updating = true;
        
        // update systems
        for (EngineSystem p : systems) if (p.isEnabled()) {
            p.update(dt);
        }
        
        // execute pending commands
        for (Command cmd : commands) cmd.execute();
        commands.clear();
        
        updating = false;
    }

    public void dispose() {
        if (updating) return;

        for (Entity e : entities) if (e.isEnabled()) {
            e.disable();
            e.removeEngine();
        }
        entities.clear();
        views.clear();

        for (int i = systems.size() - 1; i >= 0; i--) {
            EngineSystem p = systems.get(i);
            p.disable();
            p.onEngineUnbind(this);
            p.unbind();
        }
        
        systems.clear();
    }

    public List<Entity> getMembersOf(Family family) {
        List<Entity> view = views.get(family);

        if (view == null) {
            view = new ArrayList<Entity>();
            views.put(family, view);

            initView(family, view);
        }

        return Collections.unmodifiableList(view);
    }

    public Entity getEntity(int index) {
        return entities.get(index);
    }

    public Entity getEntity(UUID uuid) {
        return entitiesById.get(uuid);
    }
    
    private void initView(Family family, List<Entity> view) {
        if (!view.isEmpty()) return;

        for (Entity e : entities) if (family.isMember(e)) {
            view.add(e);
        }
    }

    public void addEntityListener(EntityListener listener, Family family) {
        List<EntityListener> listeners = filteredListeners.get(family);
        if (listeners == null) {
            listeners = new ArrayList<EntityListener>();
            filteredListeners.put(family, listeners);
        }

        if (listeners.contains(listener)) return;
        listeners.add(listener);
    }

    public void addEntityListener(EntityListener listener) {
        if (listeners.contains(listener)) return;
        listeners.add(listener);
    }

    public void removeEntityListener(EntityListener listener, Family family) {
        List<EntityListener> listeners = filteredListeners.get(family);
        if (listeners == null) return;
        listeners.remove(listener);
    }
    
    public void removeEntityListener(EntityListener listener) {
        listeners.remove(listener);
    }
    
    public void addEntity(Entity e) {
        if (updating) commands.add(() -> { addEntityInternal(e); });
        else addEntityInternal(e);
    }

    private void addEntityInternal(Entity e) {
        if (e.getEngine() != null || entities.contains(e)) throw new KronosError(KronosErrorLibrary.ENTITY_ALREADY_ADDED_TO_ENGINE);
        if (e.isEnabled()) throw new KronosError(KronosErrorLibrary.ENTITY_ALREADY_ENABLED);
        
        entities.add(e);
        entitiesById.put(e.getUUID(), e);
        e.setEngine(this);
        e.enable();
        
        addEntityToViews(e);

        for (EntityListener l : listeners) l.onEntityAdd(e);
        
        for (Entry<Family, List<EntityListener>> entry : filteredListeners.entrySet()) {
        	if (entry.getKey().isMember(e)) {
                for (EntityListener l : entry.getValue()) { l.onEntityAdd(e); }
        	}
        }
    }

    private void addEntityToViews(Entity e) {
        for (Family family : views.keySet()) if (family.isMember(e)) {
            views.get(family).add(e);
        }
    }

    public void removeEntity(Entity e) {
        if (updating) commands.add(() -> { removeEntityInternal(e); });
        else removeEntityInternal(e);
    }

    private void removeEntityInternal(Entity e) {
        if (e.getEngine() != this) return;
        if (!e.isEnabled()) throw new KronosError(KronosErrorLibrary.ENTITY_NOT_ENABLED);
        if (!entities.contains(e)) throw new KronosError(KronosErrorLibrary.ENTITY_NOT_ADDED_TO_THIS_ENGINE);

        // inform listeners as long as the entity is still active
        for (EntityListener l : listeners) l.onEntityRemove(e);
        
        for (Entry<Family, List<EntityListener>> entry : filteredListeners.entrySet()) {
        	if (entry.getKey().isMember(e)) {
                for (EntityListener l : entry.getValue()) { l.onEntityRemove(e); }
        	}
        }
        
        // actually remove entity
        e.disable();
        e.removeEngine();
        entities.remove(e);
        entitiesById.remove(e.getUUID());
       
        removeEntityFromViews(e);        
    }

    private void removeEntityFromViews(Entity e) {
        for (List<Entity> view : views.values()) view.remove(e);
    }

    public void removeAll() {
        if (updating) commands.add(() -> { removeAllInternal(); });
        else removeAllInternal();
    }

    private void removeAllInternal() {
        while (!entities.isEmpty()) removeEntityInternal(entities.get(0));
    }

    public void addSystem(EngineSystem p) throws IllegalStateException, IllegalArgumentException {
        if (updating) throw new IllegalStateException("cannot add system while updating");
        
        if (systems.contains(p)) throw new IllegalArgumentException("system already added");
        
        p.bind(this);
        systems.add(p);
        p.onEngineBind(this);
    }

    public void removeSystem(EngineSystem p) throws IllegalStateException, IllegalArgumentException {
        if (updating) throw new IllegalStateException("cannot remove system while updating");
        
        if (!systems.contains(p)) throw new IllegalArgumentException("system is unknown");
        
        p.onEngineUnbind(this);
        systems.remove(p);
        p.unbind();        
    }

    public boolean hasSystem(Class<?> clazz) {
        for (EngineSystem p : systems) if (p.getClass() == clazz) {
            return true;
        }

        return false;
    }

    public <T> T getSystem(Class<T> clazz) throws IllegalArgumentException {
        for (EngineSystem p : systems) if (clazz.isInstance(p)) {
            return clazz.cast(p);
        }
        
        throw new IllegalArgumentException("system not found " + clazz.getName());
    }
    
    public EngineSystem getSystem(int index) {
        return systems.get(index);
    }

    public boolean hasSystem(EngineSystem p) {
        return systems.contains(p);
    }
    
    public int systemCount() {
        return systems.size();
    }

    public int entityCount() {
        return entities.size();
    }
    
}
