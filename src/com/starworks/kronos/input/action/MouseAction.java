package com.starworks.kronos.input.action;

import com.starworks.kronos.event.Event;
import com.starworks.kronos.event.EventType;
import com.starworks.kronos.maths.Vector2f;

public final class MouseAction<T extends Enum<T> & ActionMap> extends Action<T> {

	protected MouseAction(T actionKey) {
		super(actionKey, EventType.MOUSE_PRESSED);
	}

	@Override
	protected final Event computeBindingsValue(Event event) {
		if (m_bindings.length < 1) return null; // at least 1 binding required
		int button = ((Event.MouseButtonPressed) event).getButton();
		
		if (!validateBinding(button)) {
			return null;
		}
		return event;
	}

	@Override
	protected final Float computeAxis1DValue(Event event) {
		int button = ((Event.MouseButtonPressed) event).getButton();
		if (!validateBinding(button)) {
			return null;
		}
		float result = button == m_bindings[0] ? -1 : button == m_bindings[1] ? 1 : 0;
		return result;
	}

	@Override
	protected final Vector2f computeAxis2DValue(Event event) {
		if (m_bindings.length < 2) return null; // 4 bindings required
		int button = ((Event.MouseButtonPressed) event).getButton();
		if (!validateBinding(button)) {
			return null;
		}
		Vector2f result = new Vector2f(button == m_bindings[2] ? -1 : button == m_bindings[3] ? 1 : 0,
									   button == m_bindings[0] ? -1 : button == m_bindings[1] ? 1 : 0);
		return result;
	}
}
