package net.acidfrog.kronos.scene.ecs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.acidfrog.kronos.scene.ecs.process.EngineProcess;

public class Engine {

    private List<Entity> entities = new ArrayList<Entity>();
    private Map<Family, List<Entity>> familyMap = new HashMap<Family, List<Entity>>();
    private List<Command> commands = new ArrayList<Command>();
    private List<EngineProcess> processes = new ArrayList<EngineProcess>();
    private Map<Family, List<EntityListener>> filteredListeners = new HashMap<Family, List<EntityListener>>();
    private boolean updating = false;
    
        
    public final List<Entity> getMembersOf(Family family) {
        return null;
    }
    
}
