package com.starworks.kronos.input;

import java.util.HashMap;
import java.util.Map;

import com.starworks.kronos.event.EventManager;
import com.starworks.kronos.input.action.Action;
import com.starworks.kronos.input.action.ActionCallback;
import com.starworks.kronos.input.action.ActionMap;

public class ActionManager<T extends Enum<T> & ActionMap> {

	private final EventManager m_events;
	private final Map<T, Action<T>> m_actions;

	ActionManager(EventManager eventManager, Class<T> actionMapType) {
		this.m_events = eventManager;
		this.m_actions = new HashMap<T, Action<T>>();
		
		for (var key : actionMapType.getEnumConstants()) {
			m_actions.put(key, Action.create(key));
		}
	}

	public void bindAction(T action, ActionCallback callback) {
		m_events.register(action.getEventType(), e -> {
			var object = m_actions.get(action);
			if (object == null) return false;
			var value = object.getValue(e);
			if (value == null) return false;
			callback.accept(value);
			return true;
		});
	}
}
