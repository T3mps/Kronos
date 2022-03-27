package net.acidfrog.kronos.scene.ecs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.acidfrog.kronos.scene.ecs.process.EngineProcess;

public class Engine {

    private List<Entity> entities = new ArrayList<Entity>();
    private Map<Family, List<Entity>> views = new HashMap<Family, List<Entity>>();
    private List<Command> commands = new ArrayList<Command>();
    private List<EngineProcess> processes = new ArrayList<EngineProcess>();
    private List<EntityListener> listeners = new ArrayList<EntityListener>();
    private Map<Family, List<EntityListener>> filteredListeners = new HashMap<Family, List<EntityListener>>();
    private boolean updating = false;
    
    public void update(float dt) {
        // assert updating == false;
        if (updating) return;
        updating = true;
        
        // update systems
        for (EngineProcess p : processes) if (p.isEnabled()) {
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

        for (int i = processes.size() - 1; i >= 0; --i) {
            EngineProcess p = processes.get(i);
            p.disable();
            p.onEngineUnbind(this);
            p.unbind();
        }
        processes.clear();
    }

    public List<Entity> getEntities(Family family) {
        List<Entity> view = views.get(family);
        if (view == null) {
            view = new ArrayList<>();
            views.put(family, view);
            initView(family, view);
        }
        return Collections.unmodifiableList(view);
    }
    
    private void initView(Family family, List<Entity> view) {
        // assert view.isEmpty();
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
        if (e.getEngine() != null) {
            throw new IllegalArgumentException(
                    "entity already added to an engine");
        }
        assert e.getEngine() == null;
        assert !e.isEnabled();
        assert !entities.contains(e);
        
        entities.add(e);
        e.defineEngine(this);
        e.enable();
        
        addEntityToViews(e);

        // inform listeners
        for (EntityListener l : listeners) l.onEntityAdd(e);
        
        for (Entry<Family, List<EntityListener>> entry : filteredListeners.entrySet()) {
        	if (entry.getKey().isMember(e)) {
                for (EntityListener l : entry.getValue()) { l.onEntityAdd(e); }
        	}
        }
    }

    private void addEntityToViews(Entity e) {
        for (Family family : views.keySet()) {
            if (family.isMember(e)) {
                views.get(family).add(e);
            }
        }
    }

    public void removeEntity(Entity e) {
        if (updating) commands.add(() -> { removeEntityInternal(e); });
        else removeEntityInternal(e);
    }

    private void removeEntityInternal(Entity e) {
        if (e.getEngine() != this) {
            // silently ignore this event (best practice)
            return;
        }
        assert e.getEngine() == this;
        assert e.isEnabled();
        assert entities.contains(e);

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

    public void addProcess(EngineProcess p) throws IllegalStateException, IllegalArgumentException {
        if (updating) throw new IllegalStateException("cannot add system while updating");
        
        if (processes.contains(p)) throw new IllegalArgumentException("system already added");
        
        p.bind(this);
        processes.add(p);
        p.onEngineBind(this);
    }

    public void removeProcess(EngineProcess p) throws IllegalStateException, IllegalArgumentException {
        if (updating) throw new IllegalStateException("cannot remove system while updating");
        
        if (!processes.contains(p)) throw new IllegalArgumentException("system is unknown");
        
        p.onEngineUnbind(this);
        processes.remove(p);
        p.unbind();        
    }

    public boolean hasProcess(Class<?> clazz) {
        for (EngineProcess p : processes) if (p.getClass() == clazz) {
            return true;
        }

        return false;
    }

    public <T> T getProcess(Class<T> clazz) throws IllegalArgumentException {
        for (EngineProcess p : processes) if (clazz.isInstance(p)) {
            return clazz.cast(p);
        }
        
        throw new IllegalArgumentException("system not found " + clazz.getName());
    }

    public final List<Entity> getMembersOf(Family family) {
        return null;
    }
    
    public EngineProcess getProcess(int index) {
        return processes.get(index);
    }
    
    public int processCount() {
        return processes.size();
    }


    public int entityCount() {
        return entities.size();
    }

}
