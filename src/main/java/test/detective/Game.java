package test.detective;

import net.acidfrog.kronos.perspective.Terminal;

public class Game implements Runnable {
    
    private static final int WIDTH = 32;
    private static final int HEIGHT = 42;

    private Thread thread;
    private Terminal terminal;
    private World world;
    private boolean running;

    public Game() {
        this.terminal = new Terminal(HEIGHT, WIDTH); // 42 rows, 32 columns
        this.world = new World("test.world");
    }

    public void start() {
		running = true;
		thread = new Thread(this, "Main");
		thread.start();
	}

    public void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

    public void run() {
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1_000_000_000.0 / 64.0;
		double delta = 0;
		int frames = 0;
		int ticks = 0;

		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;

			while (delta >= 1) {
				update();
				ticks++;
				delta--;
			}
            
            render();
			frames++;
		}
	}

    private void update() {
    }

    private void render() {
        terminal.render();
        world.render(terminal);
        terminal.drawString(3, 3, "Hello, World!", 0xff0000);
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
}
