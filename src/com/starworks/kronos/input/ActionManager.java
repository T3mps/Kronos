package com.starworks.kronos.input;

import com.starworks.kronos.event.Event;
import com.starworks.kronos.event.EventManager;
import com.starworks.kronos.input.action.ActionCallback;
import com.starworks.kronos.input.action.ActionMap;
import com.starworks.kronos.input.action.ActionProcessor;
import com.starworks.kronos.maths.Vector2f;

public class ActionManager<T extends Enum<T> & ActionMap> {

	private final EventManager m_events;

	ActionManager(EventManager eventManager, Class<T> actionMapType) {
		this.m_events = eventManager;
	}

	public void bindAction(T action, ActionCallback<Object> callback) {
		m_events.register(action.getEventType(), e -> {
			var value = new ActionValue(calculateValue(action, e)).process(action.getProcessor());
			if (value != null) {
				callback.accept(value);
			}
			return value != null;
		});
	}

	private Object calculateValue(T action, Event event) {
		return switch (action.getType()) {
		case BINDING -> computeBindingsValue(action, event);
		case AXIS_1D -> computeAxis1DValue(action, (Event.KeyPressed) event);
		case AXIS_2D -> computeAxis2DValue(action, (Event.KeyPressed) event);
		default -> null;
		};
	}

	private Event computeBindingsValue(T action, Event event) {
		int binding;
		int[] bindings;

		switch (event.getType()) {
		case KEY_PRESSED:
			binding = ((Event.KeyPressed) event).getKeyCode();
			bindings = action.getBindings();
			break;
		case MOUSE_PRESSED:
			binding = ((Event.MouseButtonPressed) event).getButton();
			bindings = action.getBindings();
			break;
		default:
			return null;
		}
		if (!validateBindings(binding, bindings)) {
			return null;
		}
		return action.getEventType().cast(event);
	}

	private Float computeAxis1DValue(T action, Event.KeyPressed event) {
		if (!action.getEventType().equals(Event.KeyPressed.class)) return null; // TODO: in the future, expand to add joystick/gamepad support
		int keyCode = event.getKeyCode();
		int[] axisBindings = action.getBindings();
		if (!validateBindings(keyCode, axisBindings)) {
			return null;
		}
		float result = keyCode == axisBindings[0] ? -1 : keyCode == axisBindings[1] ? 1 : 0;
		return result;
	}

	private Vector2f computeAxis2DValue(T action, Event.KeyPressed event) {
		if (!action.getEventType().equals(Event.KeyPressed.class)) return null; // TODO: in the future, expand to add joystick/gamepad support
		int keyCode = event.getKeyCode();
		int[] axisBindings = action.getBindings();
		if (!validateBindings(keyCode, axisBindings)) {
			return null;
		}
		Vector2f result = new Vector2f(keyCode == axisBindings[2] ? -1 : keyCode == axisBindings[3] ? 1 : 0, keyCode == axisBindings[0] ? -1 : keyCode == axisBindings[1] ? 1 : 0);
		return result;
	}

	private boolean validateBindings(int keyCode, int[] bindings) {
		boolean valid = false;
		for (int binding : bindings) {
			if (valid = binding == keyCode) break;
		}
		return valid;
	}

	private record ActionValue(Object value) {

		public Object process(ActionProcessor processor) {
			if (processor == null) {
				return value;
			}
			return processor.process(value);
		}
	}
}
