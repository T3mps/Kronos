package com.starworks.kronos.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

final class EventDispatcher<E extends Event> {

	private final List<EventCallback<E>> m_callbacks;

	public EventDispatcher() {
		this.m_callbacks = new CopyOnWriteArrayList<EventCallback<E>>();
	}

	public <C extends EventCallback<E>>void add(C callback) {
		m_callbacks.add(callback);
	}

	public void remove(EventCallback<E> callback) {
		m_callbacks.remove(callback);
	}

	public void dispatch(E event) {
		for (var callback : m_callbacks) {
			callback.accept(event);
		}
	}

	public void clear() {
		m_callbacks.clear();
	}
}
