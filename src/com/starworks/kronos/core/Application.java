package com.starworks.kronos.core;

import com.starworks.kronos.Configuration;
import com.starworks.kronos.event.Event;
import com.starworks.kronos.event.EventManager;
import com.starworks.kronos.files.FileSystem;
import com.starworks.kronos.logging.Logger;

public abstract class Application implements AutoCloseable {
	private final Logger LOGGER = Logger.getLogger(Application.class);

	private static final double NANOS_PER_SECOND = 1e9;

	protected String m_workingDirectory;
	protected EventManager m_events;
	protected Window m_window;
	protected LayerStack m_layers;
	protected boolean m_running;
	private final TimeStep m_timeStep;
	private final TimeStep m_fixedTimeStep;
	private final double m_fixedUpdateRate;
	private long m_currentTime;
	private double m_deltaTime;
	private long m_lastTime;

	public Application() {
		int width = Configuration.WINDOW_WIDTH;
		int height = Configuration.WINDOW_HEIGHT;
		String title = Configuration.WINDOW_TITLE;
		double updateRate = Configuration.UPDATE_RATE;
		double fixedUpdateRate = Configuration.FIXED_UPDATE_RATE;
		this.m_workingDirectory = Configuration.WORKING_DIRECTORY;
		this.m_events = new EventManager();
		this.m_window = Window.create(width, height, title, e -> onEvent(e));
		this.m_layers = new LayerStack();
		this.m_running = false;
		this.m_timeStep = new TimeStep(updateRate);
		this.m_fixedTimeStep = new TimeStep(fixedUpdateRate);
		this.m_fixedUpdateRate = Configuration.FIXED_UPDATE_RATE;
		this.m_lastTime = 0;

		m_events.register(Event.WindowClosed.class, e -> stop());
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

	protected final boolean onEvent(Event event) {
		if (m_layers.onEvent(event)) {
			return true;
		}
		m_events.post(event);
		return false;
	}

	public final void loop() {
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
	}

	protected abstract void update(TimeStep timestep);

	protected abstract void fixedUpdate(TimeStep timeStep);

	protected abstract void render();

	@Override
	public void close() {
		m_window.close();
		LOGGER.close();
		FileSystem.shutdown();
	}
}
