package com.starworks.kronos.core;

import com.starworks.kronos.event.Event;

public abstract class Layer {
	
	private boolean m_enabled;

	public Layer() {
		this.m_enabled = true;
	}
	
	public void onAttach() {
		// Called when the layer is added to the layer stack
	}

	public void onDetach() {
		// Called when the layer is removed from the layer stack
	}

	public void onUpdate(TimeStep timeStep) {
		// Called on update
	}
	
	public void onImGuiRender() {
		// Called during ImGui render phase
	}
	
	public boolean onEvent(Event event) {
		// Called when an event is posted and passed to the layer
		// Should return true if the event is consumed, false otherwise
		return false;
	}

	public boolean isEnabled() {
		return m_enabled;
	}

	public void setEnabled(boolean enabled) {
		m_enabled = enabled;
	}
}
