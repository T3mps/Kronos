package com.starworks.kronos.core;

import java.util.ArrayDeque;
import java.util.Deque;

import com.starworks.kronos.event.Event;

public class LayerStack {

	private final Deque<Layer> m_layers;

	public LayerStack() {
		this.m_layers = new ArrayDeque<Layer>();
	}

	public void pushLayer(Layer layer) {
		m_layers.push(layer);
		layer.onAttach();
	}

	public void pushOverlay(Layer overlay) {
		m_layers.add(overlay);
		overlay.onAttach();
	}

	public Layer popLayer() {
		var layer = m_layers.pop();
		layer.onDetach();
		return layer;
	}
	
	public Layer popOverlay() {
		var overlay = m_layers.removeLast();
		overlay.onDetach();
		return overlay;
	}
	
	public void popLayer(Layer layer) {
		m_layers.remove(layer);
		layer.onDetach();
	}

	public void update(TimeStep timestep) {
		for (var layer : m_layers) {
			if (layer.isEnabled()) {
				layer.onUpdate(timestep);
			}
		}
	}

	public boolean onEvent(Event event) {
		for (var it = m_layers.descendingIterator(); it.hasNext();) {
			var layer = it.next();
			if (layer.isEnabled()) {
				if (layer.onEvent(event)) {
					return true; // return true if layer consumes event
				}
			}
		}
		return false; // false otherwise
	}

	public void clear() {
		m_layers.clear();
	}
}
