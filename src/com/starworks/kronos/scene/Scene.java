package com.starworks.kronos.scene;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.starworks.kronos.Configuration;
import com.starworks.kronos.core.TimeStep;
import com.starworks.kronos.ecs.Entity;
import com.starworks.kronos.ecs.EventSink;
import com.starworks.kronos.ecs.EventSink.ListenerType;
import com.starworks.kronos.ecs.Registry;
import com.starworks.kronos.ecs.Scheduler;
import com.starworks.kronos.logging.Logger;
import com.starworks.kronos.scene.component.IDComponent;
import com.starworks.kronos.scene.component.TagComponent;
import com.starworks.kronos.scene.component.TransformComponent;

public final class Scene implements Closeable {
	private final Logger LOGGER = Logger.getLogger(Scene.class);

	private double m_time;
	private double m_fixedTime;
	private final double m_updateRate;
	private final double m_fixedUpdateRate;
	private int m_steps;
	private int m_fixedSteps;

	private boolean m_isRunning;
	private boolean m_isPaused;

	private final Registry m_registry;
	private final Scheduler m_scheduler;

	private final Map<UUID, Entity> m_entityMap;

	public Scene() {
		this.m_time = 0.0;
		this.m_updateRate = Configuration.runtime.updateRate();
		this.m_fixedTime = 0.0;
		this.m_fixedUpdateRate = Configuration.runtime.fixedUpdateRate();
		this.m_steps = 0;
		this.m_isRunning = false;
		this.m_isPaused = false;
		this.m_registry = new Registry();
		this.m_scheduler = m_registry.createScheduler();
		this.m_entityMap = new HashMap<UUID, Entity>();

		initialize();
	}

	private void initialize() {
		eventSink().connect(ListenerType.ON_COMPONENT_ADD, EventSink.ANY, (entity, component) -> {
			LOGGER.debug("Added {0} to {1}", component.getClass().getSimpleName(), entity.getFormattedID());
		});
		eventSink().connect(ListenerType.ON_COMPONENT_REPLACE, EventSink.ANY, (entity, component) -> {
			LOGGER.debug("Replaced {0} on {1}", component.getClass().getSimpleName(), entity.getFormattedID());
		});
		eventSink().connect(ListenerType.ON_COMPONENT_REMOVE, EventSink.ANY, (entity, component) -> {
			LOGGER.debug("Removed {0} from {1}", component.getClass().getSimpleName(), entity.getFormattedID());
		});
	}

	private static long s_unnamedEntityCount = 0;

	public Entity createEntity(String name) {
		return createEntityWithUUID(UUID.randomUUID(), name);
	}

	public Entity createEntityWithUUID(UUID uuid, String name) {
		var idComponent = new IDComponent(uuid);
		var tagComponent = new TagComponent(name.equals("") || name == null ? "Entity" + s_unnamedEntityCount++ : name);
		var transformComponent = new TransformComponent();
		var entity = m_registry.emplace(idComponent, tagComponent, transformComponent);
		m_entityMap.put(idComponent.uuid(), entity);
		return entity;
	}

	public void destroyEntity(Entity entity) {
		m_entityMap.remove(entity.get(IDComponent.class).uuid());
		m_registry.destroy(entity);
	}

	public Entity findEntityByUUID(UUID uuid) {
		var entity = m_entityMap.get(uuid);
		return entity;
	}

	public Entity findEntityByName(String name) {
		var entity = m_registry.view(TagComponent.class).stream().filter(e -> e.component().tag().equals(name)).findFirst().get().entity();
		return entity;
	}

	public void onUpdate(TimeStep timestep) {
		m_time += timestep.getDeltaTime();

		if (m_time >= m_updateRate) {
			m_scheduler.update();
			++m_steps;
		}
	}

	public void onFixedUpdate(TimeStep timestep) {
		m_fixedTime += timestep.getDeltaTime();

		if (m_fixedTime >= m_fixedUpdateRate) {
			++m_fixedSteps;
		}
	}

	public GameSystem scheduleSystem(GameSystem system) {
		m_scheduler.schedule(system);
		return system;
	}

	public GameSystem[] scheduleParallelSystems(GameSystem... systems) {
		m_scheduler.scheduleParallel(systems);
		return systems;
	}

	public void suspendSystem(GameSystem system) {
		m_scheduler.suspend(system);
	}

	public void resumeSystem(GameSystem system) {
		m_scheduler.resume(system);
	}

	@Override
	public void close() {
		LOGGER.info("Scene stepped {0} times", m_steps);
		LOGGER.info("Scene fix-stepped {0} times", m_fixedSteps);
		m_registry.close();
	}

	public Registry registry() {
		return m_registry;
	}

	public EventSink eventSink() {
		return m_registry.eventSink();
	}
}
