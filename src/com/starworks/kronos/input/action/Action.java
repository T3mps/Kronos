package com.starworks.kronos.input.action;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.starworks.kronos.event.Event;
import com.starworks.kronos.event.EventType;
import com.starworks.kronos.maths.Vector2f;

public sealed abstract class Action<T extends Enum<T> & ActionMap> permits KeyboardAction<T>, MouseAction<T> {

	protected final T m_actionKey;
	protected final int[] m_bindings;
	protected final Set<Integer> m_bindingsSet;
	protected final boolean m_hasProcessors;
	protected final EventType[] m_processedEvents;
	private final Consumer<Event> m_computeFunction;
	private Object m_value;

	protected Action(T actionKey, EventType... processedEventTypes) {
		this.m_actionKey = actionKey;
		this.m_bindings = actionKey.getBindings();
		this.m_bindingsSet = uniqueBindings();
		this.m_hasProcessors = actionKey.getProcessors() != null;
		this.m_processedEvents = processedEventTypes;
		this.m_computeFunction = switch (actionKey.getType()) {
		case BINDING -> e -> m_value = computeBindingsValue(e);
		case AXIS_1D -> e -> m_value = computeAxis1DValue(e);
		case AXIS_2D -> e -> m_value = computeAxis2DValue(e);
		default		 -> e -> m_value = null;
		};
	}

	public static <T extends Enum<T> & ActionMap> Action<T> create(T actionKey) {
		var actionEventType = actionKey.getEventType();
		if (Event.getCategory(actionEventType) == (Event.CATEGORY_INPUT | Event.CATEGORY_KEYBOARD)) return new KeyboardAction<T>(actionKey);
		if (Event.getCategory(actionEventType) == (Event.CATEGORY_INPUT | Event.CATEGORY_MOUSE)) return new MouseAction<T>(actionKey);
		throw new IllegalStateException();
	}

	public final Object getValue(Event event) {
		if (!this.processesEvent(event)) return null;
		m_computeFunction.accept(event);
		
		if (!m_hasProcessors) {
			return m_value;
		}

		var processors = m_actionKey.getProcessors();
		for (var processor : processors) {
			processor.process(m_value);
		}
		
		return m_value;
	}

	protected abstract Object computeBindingsValue(Event event);

	protected abstract Float computeAxis1DValue(Event event);

	protected abstract Vector2f computeAxis2DValue(Event event);

	private final HashSet<Integer> uniqueBindings() {
		HashSet<Integer> set = new HashSet<Integer>(m_bindings.length);
		for (int binding : m_bindings) {
			set.add(binding);
		}
		return set;
	}
	
	protected final boolean validateBinding(int binding) {
		return m_bindingsSet.contains(binding);
	}

	private final boolean processesEvent(Event event) {
		for (var eventType : m_processedEvents) {
			if (eventType == event.getType()) {
				return true;
			}
		}
		return false;
	}
}
