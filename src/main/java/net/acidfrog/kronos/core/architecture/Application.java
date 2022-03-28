package net.acidfrog.kronos.core.architecture;

import net.acidfrog.kronos.core.lang.logger.Logger;
import net.acidfrog.kronos.core.util.Chrono;

public class Application extends AbstractApplication {

    protected static int fps = 60;
	protected static int ups = 60;

    public Application() {
        super();
    }

    @Override
    protected void initialize() {
    }

    public void start() {
        super.start();
    }

    @Override
    public void run() {
        super.run();

        // this.requestFocus();
		boolean render = false;
		long lastTime = Chrono.now();
		double amountOfTicks = 60.0;
		double ns = 1_000_000_000 / amountOfTicks;
		float deltaTime = 0;
		long timer = Chrono.nowMillis();
		int ticks = 0;
		int frames = 0;

		while (running) {
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

    }

    public void stop() {
        super.stop();
        close();
    }

    public void close() {
        super.close();
    }

}
