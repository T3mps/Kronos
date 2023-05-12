package com.starworks.kronos.input;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.starworks.kronos.event.EventManager;
import com.starworks.kronos.input.action.ActionMap;

public class InputManager {
	
    private final Map<Class<? extends Enum<?>>, ActionManager<?>> actionManagers;
    private final EventManager m_eventManager;

    public InputManager(EventManager eventManager) {
        this.m_eventManager = eventManager;
        this.actionManagers = new ConcurrentHashMap<>();
    }

    public <T extends Enum<T> & ActionMap> ActionManager<T> getOrCreateActionManager(Class<T> actionMapType) {
    	var actionManager = getActionManager(actionMapType);
    	if (actionManager == null) {
    		actionManager = new ActionManager<T>(m_eventManager, actionMapType);
    		actionManagers.put(actionMapType, actionManager);
    	}
        return actionManager;
    }

    @SuppressWarnings("unchecked")
	public <T extends Enum<T> & ActionMap> ActionManager<T> getActionManager(Class<T> actionSet) {
        return (ActionManager<T>) actionManagers.get(actionSet);
    }
}