package com.starworks.kronos.core;

import com.starworks.kronos.Configuration;
import com.starworks.kronos.core.Window.WindowLayer;
import com.starworks.kronos.event.Event;
import com.starworks.kronos.event.EventCallback;
import com.starworks.kronos.event.EventManager;
import com.starworks.kronos.files.FileSystem;
import com.starworks.kronos.jobs.Job;
import com.starworks.kronos.jobs.JobManager;
import com.starworks.kronos.jobs.Jobs;
import com.starworks.kronos.logging.Logger;

import imgui.ImGui;

public abstract class Application implements AutoCloseable {
	private final Logger LOGGER = Logger.getLogger(Application.class);

	private static final double NANOS_PER_SECOND = 1e9;

	protected EventManager m_events;
	protected Window m_window;
	protected LayerStack m_layers;
	protected JobManager m_jobs;
	private final TimeStep m_timeStep;
	private final TimeStep m_fixedTimeStep;
	private final double m_fixedUpdateRate;
	private long m_currentTime;
	private double m_deltaTime;
	private long m_lastTime;
	protected boolean m_running;

	public Application() {
		int width = Configuration.window.width();
		int height = Configuration.window.height();
		String title = Configuration.window.title();
		double updateRate = Configuration.runtime.updateRate();
		double fixedUpdateRate = Configuration.runtime.fixedUpdateRate();
		this.m_events = new EventManager();
		this.m_window = Window.create(width, height, title).onEvent(e -> onEvent(e)).addWindowLayer(new WindowLayer() {
			
			@Override
			public void onUpdate() {
				ImGui.showDemoWindow();
			}
		});
		this.m_layers = new LayerStack();
		this.m_jobs = Jobs.newManager();
		this.m_timeStep = new TimeStep(updateRate);
		this.m_fixedTimeStep = new TimeStep(fixedUpdateRate);
		this.m_fixedUpdateRate = fixedUpdateRate;
		this.m_lastTime = 0;
		this.m_running = false;

		registerEvents();
		m_jobs.start();
	}
	
	protected void registerEvents() {
		registerEventListener(Event.WindowClosed.class, e -> stop());
	}

	public final <E extends Event> void registerEventListener(final Class<E> eventType, final EventCallback<E> callback) {
		m_events.register(eventType, callback);
	}
	
	public final <E extends Event> void unregisterEventListener(final Class<E> eventType, final EventCallback<E> callback) {
		m_events.unregister(eventType, callback);
	}
	
	public final void addLayer(final Layer layer) {
		m_layers.pushLayer(layer);
	}
	
	public final void addOverlay(final Layer overlay) {
		m_layers.pushOverlay(overlay);
	}
	
	public final void submitJob(final Job<?> job) {
		m_jobs.submit(job);
	}
	
	protected abstract void initialize();

	protected final void start() {
		LOGGER.info("Application starting");
		m_running = true;
		m_lastTime = System.nanoTime();
		loop();
	}

	protected final void stop() {
		LOGGER.info("Application stopping");
		m_running = false;
	}

	private final boolean onEvent(final Event event) {
		if (m_layers.onEvent(event)) {
			return true;
		}
		m_events.post(event);
		return false;
	}

	public final void loop() {
		try {
			for (; m_running ;) {
				m_currentTime = System.nanoTime();
				m_deltaTime = (m_currentTime - m_lastTime) / NANOS_PER_SECOND;
				m_lastTime = m_currentTime;
				m_timeStep.update(m_deltaTime);
				m_fixedTimeStep.update(m_fixedUpdateRate);
				m_window.update();
				update(m_timeStep);
				fixedUpdate(m_fixedTimeStep);
				render();
			}
		} catch (Exception e) {
			LOGGER.error("Unhandeled exception propagated to main loop: ", e);
		}
	}

	protected abstract void update(final TimeStep timestep);

	protected abstract void fixedUpdate(final TimeStep timeStep);

	protected abstract void render();

	@Override
	public void close() {
		m_window.close();
		m_jobs.shutdown();
		LOGGER.close();
		FileSystem.shutdown();
	}
}
