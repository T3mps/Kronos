package com.starworks.kronos.core;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import com.starworks.kronos.event.Event;

public class LayerStack implements Iterable<Layer> {

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

	public void popLayer(Layer layer) {
		m_layers.remove(layer);
		layer.onDetach();
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

	public void update(TimeStep timestep) {
		for (var layer : m_layers) {
			if (layer.isEnabled()) {
				layer.onUpdate(timestep);
			}
		}
	}

	public void imGuiRender() {
		for (var layer : m_layers) {
			if (layer.isEnabled()) {
				layer.onImGuiRender();
			}
		}
	}
	
	public boolean onEvent(Event event) {
		for (var layer : m_layers) {
			if (event.wasHandled()) break;
			if (layer.isEnabled()) {
				event.setHandled(layer.onEvent(event));
			}
		}
		return event.wasHandled();
	}

	public void clear() {
		m_layers.clear();
	}

	@Override
	public Iterator<Layer> iterator() {
		return m_layers.iterator();
	}
}
