package com.starworks.kronos.event;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

final class EventDispatcher<E extends Event> {

	private final List<PriorityEventCallback<E>> m_callbacks;

	EventDispatcher() {
		this.m_callbacks = new CopyOnWriteArrayList<PriorityEventCallback<E>>();
	}

	@SuppressWarnings("unchecked")
	void add(PriorityEventCallback<? extends Event> callback) {
        m_callbacks.add((PriorityEventCallback<E>) callback);
        m_callbacks.sort(Comparator.comparingInt(PriorityEventCallback<E>::priority).reversed());
    }

	void remove(EventCallback<? extends Event> callback) {
		m_callbacks.removeIf(wrapper -> wrapper.callback().equals(callback));
	}

	@SuppressWarnings("unchecked")
	boolean dispatch(Event event) {
		for (var wrapper : m_callbacks) {
			if (wrapper.callback().accept((E) event)) {
				return true;
			}
		}
		return false;
	}

	void clear() {
		m_callbacks.clear();
	}
}
