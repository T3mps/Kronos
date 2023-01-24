package com.starworks.kronos.ecs;

import com.starworks.kronos.toolkit.collections.pool.ChunkedPool.IDFactory;
import com.starworks.kronos.toolkit.collections.pool.ChunkedPool.Identifiable;
import com.starworks.kronos.toolkit.concurrent.AtomicUpdater;

public final class Entity implements Identifiable {

	private static final AtomicUpdater<Entity, Integer> idUpdater = AtomicUpdater.forInteger(Entity.class, "m_id");

	private volatile int m_id;
	private Registry m_registry;
	private Archetype m_archetype;
	private Object[] m_components;

	Entity(int id, Registry registry, Archetype archetype, Object... components) {
		this.setID(id);
		this.m_registry = registry;
		this.m_archetype = archetype;
		this.m_components = components;
	}

	public String serialize() {
		return m_registry.serialize(this);
	}
	
	public Entity add(Object... components) {
		return !isEnabled() ? null : m_archetype.getParentList().getRegistry().add(this, components);
	}

	public Object replace(Object component) {
		if (component == null || !isEnabled()) {
			return this;
		}
		int index = m_archetype.indexOf((Class<?>) component.getClass());
		if (m_components[index] == null) {
			return this;
		}
		m_components[index] = component;
		return this;
	}

	public Object remove(Object component) {
		return (!isEnabled() || !contains(component)) ? null : m_archetype.getParentList().getRegistry().remove(this, component.getClass());
	}

	public Object remove(Class<?> componentType) {
		return (!isEnabled() || !contains(componentType)) ? null : m_archetype.getParentList().getRegistry().remove(this, componentType);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> componentType) {
		int index = m_archetype.indexOf(componentType);
		return index < 0 ? null : (T) m_components[index];
	}

	public boolean contains(Class<?> componentType) {
		return (m_components != null && (m_archetype.length() > 1) ? m_archetype.indexOf(componentType) > -1 : m_components[0].getClass().equals(componentType));
	}

	public boolean contains(Object component) {
		int index;
		return (m_components != null && (m_archetype.length() > 1) ? ((index = m_archetype.indexOf(component.getClass())) > -1 && m_components[index].equals(component)) : m_components[0].equals(component));
	}

	public <S extends Enum<S>> void register(S signal, EventSink.ComponentEvent event) {
		m_registry.eventSink().register(this, signal, event);
	}

	public <S extends Enum<S>> void unregister(S signal) {
		m_registry.eventSink().unregister(this, signal);
	}

	@Override
	public int getID() {
		return m_id;
	}

	@Override
	public int setID(int id) {
		int prev = m_id;
		return idUpdater.compareAndSet(this, prev, id | (m_id & IDFactory.FLAG_BIT)) ? m_id : prev;
	}

	public String getFormattedID() {
		return m_registry.getEntityPool().getIDFactory().idToString(m_id);
	}

	public boolean isEnabled() {
		return m_registry.validate(this);
	}

	public Entity setEnabled(boolean enabled) {
		return (enabled && !isEnabled()) ? m_archetype.reattach(this) : (!enabled && isEnabled()) ? m_archetype.detach(this) : this;
	}

	public boolean isReleased() {
		return (m_id & IDFactory.FLAG_BIT) == IDFactory.FLAG_BIT;
	}

	public Archetype getArchetype() {
		return m_archetype;
	}

	public Object[] getComponents() {
		return m_components;
	}

	Entity setData(Archetype archetype, Object[] components) {
		this.m_archetype = archetype;
		this.m_components = components;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + m_id;
		result = prime * result + ((m_components == null) ? 0 : m_components.hashCode());
		return result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Entity[");
		sb.append("id=[");
		sb.append(m_registry.getEntityPool().getIDFactory().idToString(m_id));
		sb.append("], components={");
		if (m_components != null && m_components.length > 0) {
			for (int i = 0; i < m_components.length; i++) {
				if (i > 0) sb.append(", ");
				sb.append(m_components[i].toString());
			}
		}
		sb.append("}]");
		return sb.toString();
	}
}
