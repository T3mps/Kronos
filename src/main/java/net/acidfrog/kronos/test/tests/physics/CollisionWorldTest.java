package net.acidfrog.kronos.test.tests.physics;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferStrategy;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;

import net.acidfrog.kronos.core.Config;
import net.acidfrog.kronos.core.lang.logger.Logger;
import net.acidfrog.kronos.core.util.Chrono;
import net.acidfrog.kronos.g2drendering.Camera;
import net.acidfrog.kronos.g2drendering.G2DRenderer;
import net.acidfrog.kronos.mathk.Mathk;
import net.acidfrog.kronos.mathk.Vector2k;
import net.acidfrog.kronos.physics.geometry.Collider;
import net.acidfrog.kronos.physics.geometry.Geometry;
import net.acidfrog.kronos.physics.geometry.Transform;
import net.acidfrog.kronos.physics.world.CollisionWorld;
import net.acidfrog.kronos.physics.world.body.Body;
import net.acidfrog.kronos.physics.world.body.Material;
import net.acidfrog.kronos.physics.world.body.Rigidbody;
import net.acidfrog.kronos.test.util.InputHandler;

public class CollisionWorldTest extends Canvas implements Runnable {
    private static final long serialVersionUID = 1L;

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private static final String TITLE = "Kronos Engine | Physics Testbed";
	
	private static int fps = 60;
	private static int ups = 60;

    private Thread thread;
    private JFrame frame;
    private CollisionWorld<Rigidbody> world;

    private volatile boolean running = false;

    public CollisionWorldTest() {
        Dimension dimension = new Dimension(WIDTH, HEIGHT);

		setMinimumSize(dimension);
		setMaximumSize(dimension);
		setPreferredSize(dimension);

		Config.logEntries();

        this.frame = new JFrame(TITLE);
        this.world = new CollisionWorld<Rigidbody>();

        InputHandler.instance.initilize(this);
    }

    public synchronized void start() {
		this.running = true;
		this.thread = new Thread(this, "main_thread");
		this.thread.start();
	}

	public synchronized void stop(int n) {
		this.running = false;

		if (thread != null && n != 0) {
			try {
				Logger.instance.logWarn("Attempting to join main thread...");
				thread.join();
			} catch (InterruptedException e) {
				Logger.instance.logError("Thread join failed: " + e.getMessage());
				n++;
			}
		}

		Logger.instance.close(n);

		System.exit(n);
	}

    @Override
    public void run() {
		this.requestFocus();
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

		stop(1);
    }

	int counter = 0;

    private void update(float dt) {
		InputHandler.instance.update();
        Camera.instance.update(dt);
		G2DRenderer.update(dt);

		if (counter++ % 30 == 0) {
			Vector2k position = new Vector2k(Mathk.random(-WIDTH * 2, WIDTH * 2), Mathk.random(-HEIGHT * 2, HEIGHT * 2));
            float rotation = Mathk.randomRadians();
            Collider collider = Geometry.generatePolygon(Mathk.random(32, 128), Mathk.random(32, 128));
            Material material = Material.values()[Mathk.random(Material.values().length - 2)];
            Rigidbody rb = new Rigidbody(new Transform(position, rotation), collider, material, Body.Type.DYNAMIC);
            world.add(rb);
		}
    }

    private void physicsUpdate(float pdt) {
        world.update(pdt);
    }

    AffineTransform tx = new AffineTransform();

    private void render() {
        BufferStrategy bufferStrategy = getBufferStrategy();

		if (bufferStrategy == null) {
			createBufferStrategy(3);
			return;
		}

		Graphics2D g2d = (Graphics2D) getBufferStrategy().getDrawGraphics();

		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		tx = g2d.getTransform();
		g2d.transform(AffineTransform.getTranslateInstance(getWidth() / 2, getHeight() / 2));
		g2d.translate((int) Camera.instance.getPosition().x, (int) Camera.instance.getPosition().y);
		
		g2d.setColor(Color.WHITE);

		// the axis extends with the cameras position
		g2d.drawLine(   (int) Mathk.min(-WIDTH  * 2, -Camera.instance.getPosition().x * 2), 0, (int) Mathk.max(WIDTH  * 2, Camera.instance.getPosition().x * 2), 0);
		g2d.drawLine(0, (int) Mathk.min(-HEIGHT * 2, -Camera.instance.getPosition().y * 2), 0, (int) Mathk.max(HEIGHT * 2, Camera.instance.getPosition().y * 2));
    
        // start rendering

        world.render(g2d);

        // stop rendering

        g2d.setTransform(tx);

        g2d.dispose();
		bufferStrategy.show();
    }

    public static void main(String[] args) {
        CollisionWorldTest main = new CollisionWorldTest();

        main.frame.setResizable(false);
		main.frame.setTitle(TITLE);
		main.frame.add(main);
		main.frame.pack();
		main.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		main.frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) { main.stop(0); }
		});
		main.frame.setLocationRelativeTo(null);
		main.frame.setVisible(true);
		main.start();
    }
    
}
