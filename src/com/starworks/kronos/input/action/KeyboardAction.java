package com.starworks.kronos.input.action;

import com.starworks.kronos.event.Event;
import com.starworks.kronos.event.EventType;
import com.starworks.kronos.maths.Vector2f;

public final class KeyboardAction<T extends Enum<T> & ActionMap> extends Action<T> {
	
	protected KeyboardAction(T actionKey) {
		super(actionKey, EventType.KEY_PRESSED);
	}
	
	@Override
	protected final Event computeBindingsValue(Event event) {
		int keyCode = ((Event.KeyPressed) event).getKeyCode();
		if (!validateBinding(keyCode)) {
			return null;
		}
		return event;
	}

	@Override
	protected final Float computeAxis1DValue(Event event) {
		int keyCode = ((Event.KeyPressed) event).getKeyCode();
		if (m_bindings.length < 2) return null; // 2 bindings required
		if (!validateBinding(keyCode)) {
			return null;
		}
		float result = keyCode == m_bindings[0] ? -1 : keyCode == m_bindings[1] ? 1 : 0;
		return result;
	}

	@Override
	protected final Vector2f computeAxis2DValue(Event event) {
		int keyCode = ((Event.KeyPressed) event).getKeyCode();
		if (m_bindings.length < 4) return null; // 4 bindings required
		if (!validateBinding(keyCode)) {
			return null;
		}
		Vector2f result = new Vector2f(keyCode == m_bindings[2] ? -1 : keyCode == m_bindings[3] ? 1 : 0,
									   keyCode == m_bindings[0] ? -1 : keyCode == m_bindings[1] ? 1 : 0);
		return result;
	}
}
