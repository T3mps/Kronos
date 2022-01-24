package net.acidfrog.kronos.core;

import net.acidfrog.kronos.core.lang.input.InputHandler;
import net.acidfrog.kronos.core.lang.logger.Logger;

public class EntryPoint implements Runnable {
	
	private static int fps = 60;
	private static int ups = 60;

	private Thread thread;
	private final Window window;

	private static volatile boolean running = false;

	public EntryPoint() {
		this.window = Window.create(1024, 768, "Kronos");
		window.run();
	}

	public synchronized void start() {
		EntryPoint.running = true;

		if (thread == null) {
			thread = new Thread(this, "main_thread");
			thread.start();
		}
	}

	public synchronized void stop(int n) {
		EntryPoint.running = false;

		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Logger.instance.close(n);

		System.exit(n);
	}

	@Override
	public void run() {	
		boolean render = false;
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1_000_000_000 / amountOfTicks;
		float deltaTime = 0;
		long timer = System.currentTimeMillis();
		int ticks = 0;
		int frames = 0;

		while (running) {
			render = true;
			long now = System.nanoTime();
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

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				fps = frames;
				ups = ticks;
			}
		}

		stop(0);	
	}
	
	private void update(float dt) {
		InputHandler.instance.eof();
	}

	private void physicsUpdate(float pdt) {
	}

	private void render() {
	}

	public static void main(String[] args) {
		new EntryPoint();
	}

}
