package com.starworks.kronos.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

final class EventDispatcher<E extends Event> {

	private final List<EventCallback<E>> m_callbacks;

	public EventDispatcher() {
		this.m_callbacks = new CopyOnWriteArrayList<EventCallback<E>>();
	}

	@SuppressWarnings("unchecked")
	public void add(EventCallback<? extends Event> callback) {
		m_callbacks.add((EventCallback<E>) callback);
	}

	public void remove(EventCallback<? extends Event> callback) {
		m_callbacks.remove(callback);
	}

	@SuppressWarnings("unchecked")
	public boolean dispatch(Event event) {
		for (var callback : m_callbacks) {
			if (callback.accept((E) event)) {
				return true;
			}
		}
		return false;
	}

	public void clear() {
		m_callbacks.clear();
	}
}
