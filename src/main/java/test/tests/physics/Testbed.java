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
import java.awt.geom.Line2D;

import javax.swing.JFrame;

import net.acidfrog.kronos.core.Config;
import net.acidfrog.kronos.core.lang.logger.Logger;
import net.acidfrog.kronos.math.Mathk;
import net.acidfrog.kronos.math.Vector2k;
import net.acidfrog.kronos.physics.collision.broadphase.BroadphaseDetector;
import net.acidfrog.kronos.physics.collision.narrowphase.DistanceDetector;
import net.acidfrog.kronos.physics.collision.narrowphase.GJK;
import net.acidfrog.kronos.physics.collision.narrowphase.NarrowphaseDetector;
import net.acidfrog.kronos.physics.collision.narrowphase.RaycastResult;
import net.acidfrog.kronos.physics.collision.narrowphase.RaycastDetector;
import net.acidfrog.kronos.physics.collision.narrowphase.SAT;
import net.acidfrog.kronos.physics.collision.narrowphase.Separation;
import net.acidfrog.kronos.physics.geometry.AABB;
import net.acidfrog.kronos.physics.geometry.Circle;
import net.acidfrog.kronos.physics.geometry.Geometry;
import net.acidfrog.kronos.physics.geometry.Polygon;
import net.acidfrog.kronos.physics.geometry.Ray;
import net.acidfrog.kronos.physics.geometry.Transform;
import test.util.Camera;
import test.util.G2DRenderer;
import test.util.InputHandler;

public class Testbed extends Canvas implements Runnable {
    private static final long serialVersionUID = 1L;

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private static final String TITLE = "Kronos Engine | Physics Testbed";
	
	private static int fps = 60;
	private static int ups = 60;

    private Thread thread;
    private JFrame frame;

    private volatile boolean running = false;

    public Testbed() {
        Dimension dimension = new Dimension(WIDTH, HEIGHT);

		setMinimumSize(dimension);
		setMaximumSize(dimension);
		setPreferredSize(dimension);

		Config.logEntries();

        this.frame = new JFrame(TITLE);

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
				Logger.logInfo("FPS: " + fps + " UPS: " + ups);
				frames = 0;
				ticks = 0;
			}
		}

		stop(1);
    }
    

	// PHYSICS VARIABLES (TODO: Move to PhysicsWorld class)

	NarrowphaseDetector narrowphaseDetector = new SAT();
	RaycastDetector raycastDetector = new GJK();
	DistanceDetector distanceDetector = new GJK();

	// Circle circle1 = new Circle(50);
	Polygon polygon1 = Geometry.generateTriangle(200, 100);
	Transform transform1 = new Transform(100, 100, 0);
	RaycastResult result1 = new RaycastResult();

	Polygon polygon2 = Geometry.generatePolygon(100, 100, Geometry.MAX_POLYGON_VERTICES_COUNT);
	Transform transform2 = new Transform(300, 300, 0);
	RaycastResult result2 = new RaycastResult();

	Ray ray = new Ray(new Vector2k(0, 500), new Vector2k(1, -1));
	Separation separation = new Separation();

	boolean bounce = false, br = false;
	int x = 0, y = 1;
	int ry = 350;
	float t = 0.01f;

	// END PHYSICS VARIABLES

    public void update(float dt) {
		Camera.instance.update(dt);
		G2DRenderer.update(dt);
		InputHandler.instance.update();

		if (transform1.getPosition().x < WIDTH * 0.75f && !bounce) {
			x = 1;
			y = 1;
		} else {
			if (transform1.getPosition().x > 0) {
				bounce = true;
				x = -1;
				y = -1;
			} else {
				bounce = false;
				x = 0;
			}
		}

		transform1.transform(x, y, t += 0.01f);
		transform2.setRotation(-t);
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
		g2d.translate((int) Camera.instance.getPosition().x, (int) Camera.instance.getPosition().y);
		
		g2d.setColor(Color.WHITE);

		// the axis extends with the cameras position
		g2d.drawLine(   (int) Mathk.min(-WIDTH  * 2, -Camera.instance.getPosition().x * 2), 0, (int) Mathk.max(WIDTH  * 2, Camera.instance.getPosition().x * 2), 0);
		g2d.drawLine(0, (int) Mathk.min(-HEIGHT * 2, -Camera.instance.getPosition().y * 2), 0, (int) Mathk.max(HEIGHT * 2, Camera.instance.getPosition().y * 2));

		// start rendering

		AABB aabb1 = polygon1.computeAABB(transform1);
		AABB aabb2 = polygon2.computeAABB(transform2);
		Color ac1 = Color.CYAN;
		Color ac2 = Color.CYAN;
		boolean aabbray = BroadphaseDetector.raycast(ray, 0f, aabb1);
		boolean raycast1 = aabbray ? raycastDetector.raycast(ray, 0, polygon1, transform1, result1) : false;
		boolean raycast2 = raycastDetector.raycast(ray, 0, polygon2, transform2, result2);
		boolean collision1 = false;
		boolean sep = distanceDetector.distance(polygon1, transform1, polygon2, transform2, separation);

		if (aabb1.intersects(aabb2)) {
			collision1 = narrowphaseDetector.detect(polygon1, transform1, polygon2, transform2);
			ac1 = Color.RED;
		} else {
			ac1 = Color.CYAN;
		}
		
		boolean collision2 = false;
		if (aabbray) {
			ac1 = Color.YELLOW;
	 	} else if (aabb2.intersects(aabb1)) {
			collision2 = narrowphaseDetector.detect(polygon2, transform2, polygon1, transform1);
			ac2 = Color.RED;
		} else {
			ac2 = Color.CYAN;
		}
		
		Color c1 = collision1 ? Color.RED : raycast1 ? Color.YELLOW : Color.BLUE; 
		Color c2 = collision2 ? Color.RED : raycast2 ? Color.YELLOW : Color.BLUE;
		
		G2DRenderer.render(g2d, polygon1, transform1, c1);
		G2DRenderer.render(g2d, polygon2, transform2, c2);

		G2DRenderer.render(g2d, aabb1, ac1);
		G2DRenderer.render(g2d, aabb2, ac2);

		G2DRenderer.render(g2d, ray, Color.YELLOW);

		if (raycast1) G2DRenderer.render(g2d, new Circle(6), new Transform(result1.getPoint()), Color.MAGENTA);
		if (raycast2) G2DRenderer.render(g2d, new Circle(6), new Transform(result2.getPoint()), Color.MAGENTA);

		Line2D.Float line = new Line2D.Float(separation.getPointA().x * Camera.instance.getZoomLevel(), -separation.getPointA().y * Camera.instance.getZoomLevel(),
											 separation.getPointB().x * Camera.instance.getZoomLevel(), -separation.getPointB().y * Camera.instance.getZoomLevel());

        g2d.setColor(sep ? Color.GREEN : Color.ORANGE);
        g2d.draw(line);

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
		return Camera.instance.toWorldCoordinates(this.getWidth(), this.getHeight(), p);
	}

	static GraphicsDevice device = GraphicsEnvironment
        .getLocalGraphicsEnvironment().getScreenDevices()[0];

    public static void main(String[] args) {
        Testbed main = new Testbed();

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
