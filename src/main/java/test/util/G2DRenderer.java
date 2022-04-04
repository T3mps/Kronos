package test.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.BasicStroke;

import net.acidfrog.kronos.core.lang.logger.Logger;
import net.acidfrog.kronos.math.Mathk;
import net.acidfrog.kronos.math.Vector2k;
import net.acidfrog.kronos.math.Vector3f;
import net.acidfrog.kronos.physics.geometry.AABB;
import net.acidfrog.kronos.physics.geometry.Circle;
import net.acidfrog.kronos.physics.geometry.Polygon;
import net.acidfrog.kronos.physics.geometry.Ray;
import net.acidfrog.kronos.physics.geometry.Shape;
import net.acidfrog.kronos.physics.geometry.Transform;

public class G2DRenderer {

    static final int MAX_RGB_VALUE = 255;
    static final int MAX_RAYCAST = 100_000;

    static float dash[] = { 5.0f, 5.0f };
	static BasicStroke dashed = new BasicStroke();
    static final BasicStroke RESET = new BasicStroke();
	static int t = 0;

    public static void update(float dt) {
        G2DRenderer.dashed = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.5f, dash, ++t % 2 == 0 ? ++t : 0);
    }

    public static void render(Graphics2D g2d, AABB aabb, Color color) {
        float x = aabb.getMin().x;
        float y = aabb.getMin().y;
        float width = aabb.getWidth();
        float height = aabb.getHeight();

        Path2D.Float path = new Path2D.Float();
        path.moveTo(x * Camera.instance.getZoomLevel(), -y * Camera.instance.getZoomLevel());
        path.lineTo((x + width) * Camera.instance.getZoomLevel(), -y * Camera.instance.getZoomLevel());
        path.lineTo((x + width) * Camera.instance.getZoomLevel(), -(y + height) * Camera.instance.getZoomLevel());
        path.lineTo(x * Camera.instance.getZoomLevel(), -(y + height) * Camera.instance.getZoomLevel());
        path.closePath();

        g2d.setStroke(dashed);
        g2d.setColor(color);
        g2d.draw(path);
        g2d.setStroke(RESET);
    }

    public static void render(Graphics2D g2d, Shape shape, Transform transform, Color color) {
        if (shape == null) {
            Logger.logWarn("Attempted render target is null.");
            return;
        }
        if (transform == null) {
            Logger.logWarn("Attempted render transform is null.");
            return;
        }

        if (shape instanceof Circle) render(g2d, (Circle) shape, transform, color);
        if (shape instanceof Polygon) render(g2d, (Polygon) shape, transform, color);
    }

    public static void render(Graphics2D g2d, Circle circle, Transform transform, Color color) {
        float radius = circle.getRadius();
        float diameter = radius * 2;
        Vector2k center = transform.getTransformed(circle.getCenter());
        float rx = Mathk.cos(transform.getRadians()) * radius;
        float ry = Mathk.sin(transform.getRadians()) * radius;
        
        Ellipse2D.Float elipse = new Ellipse2D.Float((center.x - radius)  * Camera.instance.getZoomLevel(),
                                                     -(center.y + radius) * Camera.instance.getZoomLevel(),
                                                     (diameter)           * Camera.instance.getZoomLevel(),
                                                     (diameter)           * Camera.instance.getZoomLevel());

        Line2D.Float line = new Line2D.Float(center.x * Camera.instance.getZoomLevel(),
                                            -center.y * Camera.instance.getZoomLevel(),
                                            (center.x + rx) * Camera.instance.getZoomLevel(),
                                            (-center.y + ry) * Camera.instance.getZoomLevel());

        g2d.setColor(color);
        g2d.fill(elipse);
        g2d.setColor(invert(color));
        g2d.draw(elipse);
        g2d.draw(line);
    }

    public static void render(Graphics2D g2d, Polygon polygon, Transform transform, Color color) {
        int length = polygon.getVertexCount();
        Vector2k[] vertices = new Vector2k[length];
        Vector2k center = transform.getTransformed(polygon.getCenter());

        for (int i = 0; i < length; i++) vertices[i] = transform.getTransformed(polygon.getVertices()[i]);
        
        Path2D.Float path = new Path2D.Float();
        path.moveTo(vertices[0].x * Camera.instance.getZoomLevel(), -vertices[0].y * Camera.instance.getZoomLevel());
        for (int i = 1; i < length; i++) path.lineTo(vertices[i].x * Camera.instance.getZoomLevel(), -vertices[i].y * Camera.instance.getZoomLevel());
        path.closePath();

        g2d.setColor(color);
        g2d.fill(path);
        g2d.setColor(invert(color));
        g2d.draw(path);

        path = new Path2D.Float();
        path.moveTo(center.x * Camera.instance.getZoomLevel(), -center.y * Camera.instance.getZoomLevel());
        path.lineTo(vertices[0].x * Camera.instance.getZoomLevel(), -vertices[0].y * Camera.instance.getZoomLevel());
        path.closePath();

        g2d.draw(path);
    }

    public static void render(Graphics2D g2d, Ray ray, Color color) {
        Vector2k start = ray.getStart();
        Vector2k direction = ray.getDirectionVector();
        Vector2k end = new Vector2k(start.x + direction.x * MAX_RAYCAST, start.y + direction.y * MAX_RAYCAST);

        Line2D.Float line = new Line2D.Float(start.x * Camera.instance.getZoomLevel(), -start.y * Camera.instance.getZoomLevel(),
                                            end.x * Camera.instance.getZoomLevel(), -end.y * Camera.instance.getZoomLevel());

        g2d.setColor(color);
        g2d.draw(line);
    }

    public static Color invert(Color c) {
        int a = c.getAlpha();
        int r = MAX_RGB_VALUE - c.getRed();
        int g = MAX_RGB_VALUE - c.getGreen();
        int b = MAX_RGB_VALUE - c.getBlue();

        // if the resulting color is to light (e.g. initial color is black, resulting color is white...)
        if ((r + g + b > 740) || (r + g + b < 20)) return new Color(MAX_RGB_VALUE, MAX_RGB_VALUE, 40, a); // return a standard yellow
        else return new Color(r, g, b, a);
    }

    public static Vector3f getRBGFromHex(float hexcode) {
        if (hexcode < 0 || hexcode > 0xffffff) return new Vector3f(0, 0, 0);
        int r = ((int) hexcode & 0xFF0000) >> 16;
        int g = ((int) hexcode & 0xFF00) >> 8;
        int b = ((int) hexcode & 0xFF);
        return new Vector3f(r, g, b);
    }
    
}
