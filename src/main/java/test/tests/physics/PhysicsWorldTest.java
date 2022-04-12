package test.tests.physics;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferStrategy;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;

import javax.swing.JFrame;

import net.acidfrog.kronos.core.lang.logger.Logger;
import net.acidfrog.kronos.core.util.Chrono;
import net.acidfrog.kronos.math.Mathk;
import net.acidfrog.kronos.math.Vector2k;
import net.acidfrog.kronos.physics.Physics;
import net.acidfrog.kronos.physics.geometry.Circle;
import net.acidfrog.kronos.physics.geometry.Transform;
import net.acidfrog.kronos.physics.world.PhysicsWorld;
import net.acidfrog.kronos.physics.world.body.Material;
import net.acidfrog.kronos.physics.world.body.Rigidbody;
import test.util.G2DCamera;
import test.util.G2DRenderer;
import test.util.JavaInputHandler;

public class PhysicsWorldTest extends Canvas implements Runnable {
    private static final long serialVersionUID = 1L;

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private static final String TITLE = "Kronos Engine | Physics Testbed";
	
	private static int fps = 60;
	private static int ups = 60;

    private Thread thread;
    private JFrame frame;
	private PhysicsWorld world;

    private volatile boolean running = false;

    public PhysicsWorldTest() {
        Dimension dimension = new Dimension(WIDTH, HEIGHT);
		setMinimumSize(dimension);
		setMaximumSize(dimension);
		setPreferredSize(dimension);

        this.frame = new JFrame(TITLE);
		this.world = new PhysicsWorld();

		for (int i = -4; i < 5; i++) {
			world.add(new Rigidbody(new Transform(new Vector2k(i * 100, 0)), new Circle(64f), Material.m_Static, Rigidbody.Type.STATIC));
		}

		for (int i = 0; i < 100; i++) {
			world.add(new Rigidbody(new Transform(new Vector2k(1f, 100 + i * 40)), new Circle(Mathk.random(4f, 16f)), Material.m_Rubber, Rigidbody.Type.DYNAMIC));
		}
		
        JavaInputHandler.instance.initilize(this);
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
				Logger.logWarn("Attempting to join main thread...");
				thread.join();
			} catch (InterruptedException e) {
				Logger.logError("Thread join failed: " + e.getMessage());
				n++;
			}
		}

		Logger.close(n);

		System.exit(n);
	}

    @Override
    public void run() {
		this.requestFocus();
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
				update(deltaTime);
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
				Logger.logInfo("FPS: " + fps + " UPS: " + ups);
				frames = 0;
				ticks = 0;
			}
		}

		stop(1);
    }

    public void update(float dt) {
		G2DCamera.instance.update(dt);
		G2DRenderer.update(dt);
		JavaInputHandler.instance.update();
		world.update(Physics.DT);
	}
	
    public void physicsUpdate(float pdt) {
    }

	AffineTransform tx = new AffineTransform();

    public void render() {
        BufferStrategy bufferStrategy = getBufferStrategy();

		if (bufferStrategy == null) {
			createBufferStrategy(3);
			return;
		}

		Graphics2D g2d = (Graphics2D) getBufferStrategy().getDrawGraphics();

		this.clear(g2d);
		tx = g2d.getTransform();
		g2d.transform(AffineTransform.getTranslateInstance(getWidth() / 2, getHeight() / 2));
		g2d.translate((int) G2DCamera.instance.getPosition().x, (int) G2DCamera.instance.getPosition().y);
		
		g2d.setColor(Color.WHITE);

		// the axis extends with the cameras position
		g2d.drawLine(   (int) Mathk.min(-WIDTH  * 2, -G2DCamera.instance.getPosition().x * 2), 0, (int) Mathk.max(WIDTH  * 2, G2DCamera.instance.getPosition().x * 2), 0);
		g2d.drawLine(0, (int) Mathk.min(-HEIGHT * 2, -G2DCamera.instance.getPosition().y * 2), 0, (int) Mathk.max(HEIGHT * 2, G2DCamera.instance.getPosition().y * 2));

		// start rendering

		world.render(g2d);

		// end rendering

		g2d.setTransform(tx);

        g2d.dispose();
		bufferStrategy.show();
	}

	private void clear(Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
	}

	public Vector2k toWorldCoordinates(Vector2k p) {
		return G2DCamera.instance.toWorldCoordinates(this.getWidth(), this.getHeight(), p);
	}

	static GraphicsDevice device = GraphicsEnvironment
        .getLocalGraphicsEnvironment().getScreenDevices()[0];

    public static void main(String[] args) {
        PhysicsWorldTest main = new PhysicsWorldTest();

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
