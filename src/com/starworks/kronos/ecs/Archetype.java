package com.starworks.kronos.ecs;

import com.starworks.kronos.toolkit.collections.pool.ChunkedPool;
import com.starworks.kronos.toolkit.collections.pool.ChunkedPool.IDFactory;

public final class Archetype {

	public static final int COMPONENT_INDEX_CAPACITY = 1 << 10;

	private final ArchetypeList m_parentList;
	private final ChunkedPool.Allocator<Entity> m_allocator;
	private final Class<?>[] m_componentTypes;
	private final int[] m_componentIndices;

	protected Archetype(ArchetypeList archetypeList, ChunkedPool.Allocator<Entity> allocator, Class<?>... componentTypes) {
		this.m_parentList = archetypeList;
		this.m_allocator = allocator;
		this.m_componentTypes = componentTypes;
		if (m_componentTypes.length < 1) {
			this.m_componentIndices = null;
			return;
		}
		this.m_componentIndices = new int[COMPONENT_INDEX_CAPACITY];
		for (int i = 0; i < componentTypes.length; i++) {
			m_componentIndices[archetypeList.getClassMap().indexOf(componentTypes[i])] = i + 1;
		}
	}

	public Entity createEntity(Registry registry, Object... components) {
		int id = m_allocator.nextID();
		return m_allocator.register(id, new Entity(id, registry, this, m_componentTypes.length > 1 ? sort(components) : components));
	}

	public boolean destroy(Entity entity) {
		m_allocator.freeID(entity.getID());
		entity.setID(entity.getID() | IDFactory.DETACHED_BIT);
		entity.setData(null, null);
		return true;
	}

	public Entity attach(Entity entity, Object... components) {
		entity = m_allocator.register(entity.setID(m_allocator.nextID()), switch (length()) {
		case 00 -> entity.setData(this, null);
		case 01 -> entity.setData(this, components);
		default -> entity.setData(this, sort(components));
		});
		return entity;
	}

	public Entity detach(Entity entity) {
		m_allocator.freeID(entity.getID());
		entity.setID(entity.getID() | IDFactory.DETACHED_BIT);
		return entity;
	}
	
	public Entity reattach(Entity entity) {
		entity.setID(m_allocator.nextID());
		m_allocator.register(entity.getID(), entity);
		return entity;
	}
	
	public Object[] sort(Object[] components) {
		int nextIndex;
		for (int i = 0; i < components.length; i++) {
			nextIndex = indexOf(components[i].getClass());
			if (nextIndex != i) {
				var temp = components[nextIndex];
				components[nextIndex] = components[i];
				components[i] = temp;
			}
		}
		nextIndex = indexOf(components[0].getClass());
		if (nextIndex > 0) {
			Object temp = components[nextIndex];
			components[nextIndex] = components[0];
			components[0] = temp;;
		}
		return components;
	}

	public int indexOf(Class<?> componentType) {
		return m_componentIndices == null ? 0 : m_componentIndices[m_parentList.getClassMap().indexOf(componentType)] - 1;
	}

	public int length() {
		return m_componentTypes.length;
	}

	public ArchetypeList getParentList() {
		return m_parentList;
	}

	public ChunkedPool.Allocator<Entity> getAllocator() {
		return m_allocator;
	}

	public Class<?>[] getComponentTypes() {
		return m_componentTypes;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Archetype[");
		for (int i = 0; i < m_componentTypes.length; i++) {
			sb.append(m_componentTypes[i].getSimpleName());
			if (i < m_componentTypes.length - 1) {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
