package net.acidfrog.kronos.core.architecture;

import net.acidfrog.kronos.core.lang.logger.Logger;
import net.acidfrog.kronos.core.util.Chrono;
import net.acidfrog.kronos.scene.SceneManager;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;

public class Application extends AbstractApplication {

	private SceneManager sceneManager;

    public Application(String windowTitle, int... args) {
        super(windowTitle, DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
    }

    public void start() {
        super.start();
		run();
    }

    @Override
    public void run() {
        super.run();

		boolean render = false;
		long lastTime = Chrono.now();
		double amountOfTicks = 60.0;
		double ns = 1_000_000_000 / amountOfTicks;
		float deltaTime = 0;
		long timer = Chrono.nowMillis();
		int ticks = 0;
		int frames = 0;

		while (running = !glfwWindowShouldClose(window.pointer())) {
			render = true;
			long now = Chrono.now();
			deltaTime += (now - lastTime) / ns;
			lastTime = now;

			while (deltaTime >= 1) {
				render = true;
				update(deltaTime);
				physicsUpdate(deltaTime);
				ticks++;
				deltaTime--;
			}

			if (render) {
				render();
				frames++; 
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (Chrono.nowMillis() - timer > 1000) {
				timer += 1000;
				fps = frames;
				ups = ticks;
				Logger.instance.logInfo("FPS: " + fps + " UPS: " + ups);
				frames = 0;
				ticks = 0;
			}
		}

		stop();
    }

    @Override
    public synchronized void update(float dt) {
    }

    @Override
    public synchronized void physicsUpdate(float dt) {

    }

    @Override
    public synchronized void render() {
		glfwPollEvents();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glfwSwapBuffers(window.pointer());
	}

	@Override
	public void stop() {
		super.stop();

		glfwDestroyWindow(window.pointer());
		glfwTerminate();
		glfwSetErrorCallback(null).free();

		close();
	}

	@Override
	public void close() {
		super.close();
		
		Logger.instance.close(0);
		
		state = state.next();
	}

}
