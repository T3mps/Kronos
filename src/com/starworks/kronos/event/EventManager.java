package com.starworks.kronos.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class EventManager {

	private final Map<Class<? extends Event>, EventDispatcher<? extends Event>> m_dispatch;

	@SuppressWarnings("unchecked")
	public EventManager() {
		this.m_dispatch = new ConcurrentHashMap<Class<? extends Event>, EventDispatcher<? extends Event>>();
		Class<Event> eventClass = Event.class;
		for (var subclass : eventClass.getClasses()) {
			if (eventClass.isAssignableFrom(subclass)) {
				m_dispatch.put((Class<? extends Event>) subclass, new EventDispatcher<>());
			}
		}
	}

	public <E extends Event> void register(Class<E> eventClass, EventCallback<E> callback, int priority) {
	    m_dispatch.get(eventClass).add(new PriorityEventCallback<E>(callback, priority));
	}

	public <E extends Event> void register(Class<E> eventClass, EventCallback<E> callback) {
	    register(eventClass, callback, PriorityEventCallback.DEFAULT_PRIORITY);
	}

	public <E extends Event> void unregister(Class<E> eventClass, EventCallback<E> callback) {
		m_dispatch.get(eventClass).remove(callback);
	}

	public boolean post(Event event) {
		return event.setHandled(m_dispatch.get(event.getClass()).dispatch(event));
	}

	public void clear() {
		m_dispatch.values().forEach(EventDispatcher::clear);
	}
}
