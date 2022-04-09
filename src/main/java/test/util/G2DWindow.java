package test.util;

import java.util.ArrayList;
import java.util.List;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.BasicStroke;
import java.awt.FontMetrics;

import net.acidfrog.kronos.math.Vector2k;
import net.acidfrog.kronos.math.Vector3f;
import net.acidfrog.kronos.physics.geometry.AABB;

public class G2DWindow {

    private String name;
    private Vector2k position;
    private Vector2k visibleSize;
    private Vector2k realSize;
    private Vector2k color;
    private int alpha;
    private int fontSize;
    private int titleMargin;
    private boolean visible;
    private boolean minimized = false;
    private AABB titleBounds;
    private AABB bounds;
    private AABB[] buttonBounds;
    private Vector2k[] scrollbarPositions;
    float[] scrollbarValues;

    private List<String> strings = new ArrayList<String>();

    private static final int DEFAULT_ALPHA = (1 << 8) - 50;
    private static final int DEFAULT_TITLE_COLOR = 0xD9534F;
    private static final int DEFAULT_CONTENT_PANE_COLOR = 0xFFEEAD;

    public G2DWindow(String name, int x, int y, int width, int height, int titleBarColor, int contentPaneColor, int transparency, boolean visible) {
        this.name = name;
        this.position = new Vector2k(x, y);
        this.visibleSize = new Vector2k(width, height);
        this.realSize = new Vector2k(width, height);
        this.color = new Vector2k(titleBarColor, contentPaneColor);
        this.alpha = transparency;
        this.fontSize = height / 11;
        this.titleMargin = height / 9;
        this.buttonBounds = new AABB[4];
        this.scrollbarPositions = new Vector2k[2];
        this.scrollbarValues = new float[2];
        setBounds();
        this.visible = true;
    }

    public G2DWindow(String name, int x, int y, int width, int height, int titleBarColor, int contentPaneColor) {
        this(name, x, y, width, height, titleBarColor, contentPaneColor, DEFAULT_ALPHA, true);
    }

    public G2DWindow(String name, int x, int y, int width, int height, int color) {
        this(name, x, y, width, height, color, color, DEFAULT_ALPHA, true);
    }

    public G2DWindow(String name, int x, int y, int width, int height) {
        this(name, x, y, width, height, DEFAULT_TITLE_COLOR, DEFAULT_CONTENT_PANE_COLOR, DEFAULT_ALPHA, true);
    }

    private void setBounds() {
        this.titleBounds = new AABB(position, position.add(new Vector2k(visibleSize.x, titleMargin)));
        this.bounds = new AABB(position, position.add(visibleSize));
        
        this.scrollbarPositions[0] = new Vector2k(position.x + visibleSize.x * 0.925f, position.y + titleMargin);
        this.scrollbarPositions[1] = new Vector2k(position.x + visibleSize.x * 0.925f, position.y + titleMargin);

        // titleButtons
        this.buttonBounds[0] = new AABB(new Vector2k(position.x + visibleSize.x * 0.8f, position.y), new Vector2k(position.x + visibleSize.x * 0.9f, position.y + titleMargin));
        this.buttonBounds[1] = new AABB(new Vector2k(position.x + visibleSize.x * 0.9f, position.y), new Vector2k(position.x + visibleSize.x, position.y + titleMargin));
    
        // scrollButtons
        this.buttonBounds[2] = new AABB(new Vector2k(scrollbarPositions[0].x, scrollbarPositions[0].y), new Vector2k(scrollbarPositions[0].x + visibleSize.x * 0.075f, scrollbarPositions[0].y + visibleSize.y * 0.075f));
        this.buttonBounds[3] = new AABB(new Vector2k(scrollbarPositions[1].x, scrollbarPositions[1].y), new Vector2k(scrollbarPositions[1].x + visibleSize.x * 0.075f, scrollbarPositions[1].y + visibleSize.y * 0.075f));
    }

    private boolean buttonDown = false;
    private boolean dragging = false;
    private boolean scrolling = false;
    Vector2k dragOffset = new Vector2k(0), scrollOffset = new Vector2k(0);

    public void show(Graphics2D g2d) {
        if (visible) {
            setBounds();

            Vector2k mouse = new Vector2k(JavaInputHandler.instance.getMouseX(), JavaInputHandler.instance.getMouseY());

            if (bounds.contains(mouse)) {
                TitleDrag: {
                    if (titleBounds.contains(mouse) && !dragging) {
                        if (JavaInputHandler.instance.isButton(1)) {
                            dragging = true;
                            dragOffset = new Vector2k(JavaInputHandler.instance.getMouseX() - position.x, JavaInputHandler.instance.getMouseY() - position.y);
                        }
                    }
        
                    if (dragging) {
                        position.x = JavaInputHandler.instance.getMouseX() - dragOffset.x;
                        position.y = JavaInputHandler.instance.getMouseY() - dragOffset.y;
                    } else break TitleDrag;
                }
    
                TitleButton: {
                    if (buttonBounds[0].contains(mouse)) {
                        if (JavaInputHandler.instance.isButton(1) && !buttonDown) {
                            minimized = !minimized;
                            buttonDown = true;
                        }
                        break TitleButton;
                    }
                    if (buttonBounds[1].contains(mouse)) {
                        if (JavaInputHandler.instance.isButton(1) && !buttonDown) {
                            visible = false;
                            dragging = false;
                            buttonDown = true;
                        }
                        break TitleButton;
                    }
                }

                ScrollBar: {
                    if (buttonBounds[2].contains(mouse)) {
                        if (JavaInputHandler.instance.isButton(1) && !buttonDown) {
                            buttonDown = true;
                            scrolling = true;
                            scrollOffset = new Vector2k(0, JavaInputHandler.instance.getMouseY());
                        }
                        if (scrolling) {
                            //
                        } else break ScrollBar;
                    }
                    if (buttonBounds[3].contains(mouse)) {
                        if (JavaInputHandler.instance.isButton(1) && !buttonDown) {
                            buttonDown = true;
                            scrolling = true;
                            scrollOffset = new Vector2k(0, JavaInputHandler.instance.getMouseY());
                        }
                        if (scrolling) {
                            //
                        } else break ScrollBar;
                    }
                }
            }

            
            if (JavaInputHandler.instance.isButtonUp(1)) {
                dragging = false;
                buttonDown = false;
                scrolling = false;
            }
            
            Path2D.Float path = new Path2D.Float();
            path.moveTo(position.x, position.y);
            path.lineTo(position.x + visibleSize.x, position.y);
            path.lineTo(position.x + visibleSize.x, position.y + titleMargin);
            path.lineTo(position.x, position.y + titleMargin);
            path.closePath();
            
            Vector3f rgb = G2DRenderer.getRBGFromHex(color.x);
            
            g2d.setColor(new Color((int) rgb.x(), (int) rgb.y(), (int) rgb.z(), alpha));
            g2d.fill(path); 

            path = new Path2D.Float();
            path.moveTo(buttonBounds[0].getMin().x, buttonBounds[0].getMin().y);
            path.lineTo(buttonBounds[0].getMax().x, buttonBounds[0].getMin().y);
            path.lineTo(buttonBounds[0].getMax().x, buttonBounds[0].getMax().y);
            path.lineTo(buttonBounds[0].getMin().x, buttonBounds[0].getMax().y);
            path.closePath();

            int r = ((int) color.x & 0xFF0000) >> 16;
            r ^= 0xff;
            int g = ((int) color.x & 0xFF00) >> 8;
            g ^= 0xff;
            int b = ((int) color.x & 0xFF);
            b ^= 0xff;

            g2d.setColor(new Color(r, g, b, alpha));
            g2d.fill(path);

            path = new Path2D.Float();
            path.moveTo(buttonBounds[1].getMin().x, buttonBounds[1].getMin().y);
            path.lineTo(buttonBounds[1].getMax().x, buttonBounds[1].getMin().y);
            path.lineTo(buttonBounds[1].getMax().x, buttonBounds[1].getMax().y);
            path.lineTo(buttonBounds[1].getMin().x, buttonBounds[1].getMax().y);
            path.closePath();

            r = ((int) color.x & 0xFF0000) >> 16;
            r &= ~0xff;
            g = ((int) color.x & 0xFF00) >> 8;
            g &= ~0xff;
            b = ((int) color.x & 0xFF);
            b &= ~0xff;

            g2d.setColor(new Color(r, g, b, alpha));
            g2d.fill(path);

            g2d.setColor(new Color(255, 255, 255, alpha));
            g2d.setFont(new Font("Consolas", Font.BOLD, fontSize));
            FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
            int x = (int) (titleBounds.getMin().x + (titleBounds.getExtents().x * 0.8f - fm.stringWidth(name)) / 2);
            int y = (int) (titleBounds.getMin().y + (titleBounds.getExtents().y - fm.getHeight()) / 2 + fm.getAscent());
            g2d.drawString(name, x, y);

            if (!minimized) {
                Shape old = g2d.getClip();
                g2d.setClip(new Rectangle((int) position.x, (int) position.y + titleMargin, (int) visibleSize.x, (int) visibleSize.y - titleMargin));
                path = new Path2D.Float();
                path.moveTo(position.x, position.y + titleMargin);
                path.lineTo(position.x + realSize.x, position.y + titleMargin);
                path.lineTo(position.x + realSize.x, position.y + visibleSize.y);
                path.lineTo(position.x, position.y + visibleSize.y);
                path.closePath();
                
                rgb = G2DRenderer.getRBGFromHex(color.y);
    
                g2d.setColor(new Color((int) rgb.x(), (int) rgb.y(), (int) rgb.z(), alpha));
                g2d.fill(path);

                g2d.setColor(new Color(255, 255, 255, alpha));
    
                int yOff = 0;
                float fs = fontSize * 0.8f;
                g2d.setFont(g2d.getFont().deriveFont(fs));
                g2d.setColor(G2DRenderer.invert(new Color((int) color.y)));
                for (int i = 0; i < strings.size(); i++) {
                    g2d.drawString(strings.get(i), (int) position.x + 1, (int) position.y + 1 + yOff + (titleMargin + (titleMargin / 2)));
                    yOff += fs + 1;
                }

                path = new Path2D.Float();
                path.moveTo(bounds.min.x, bounds.min.y);
                path.lineTo(bounds.max.x, bounds.min.y);
                path.lineTo(bounds.max.x, bounds.max.y);
                path.lineTo(bounds.min.x, bounds.max.y);
                path.closePath();

                rgb = G2DRenderer.getRBGFromHex(color.x);
    
                g2d.setColor(new Color((int) rgb.x(), (int) rgb.y(), (int) rgb.z(), alpha));
                g2d.setStroke(new BasicStroke(3f));
                g2d.draw(path);
                g2d.setStroke(new BasicStroke(1f));

                r = (int) rgb.x();
                r /= 4;
                g = (int) rgb.y();
                g /= 4;
                b = (int) rgb.z();
                b /= 4;

                path = new Path2D.Float();
                path.moveTo(buttonBounds[2].getMin().x, buttonBounds[2].getMin().y);
                path.lineTo(buttonBounds[2].getMax().x, buttonBounds[2].getMin().y);
                path.lineTo(buttonBounds[2].getMax().x, buttonBounds[2].getMax().y);
                path.lineTo(buttonBounds[2].getMin().x, buttonBounds[2].getMax().y);
                path.closePath();

                g2d.setColor(new Color(r, g, b, alpha));
                g2d.fill(path);

                path = new Path2D.Float();
                path.moveTo(buttonBounds[3].getMin().x, buttonBounds[3].getMin().y);
                path.lineTo(buttonBounds[3].getMax().x, buttonBounds[3].getMin().y);
                path.lineTo(buttonBounds[3].getMax().x, buttonBounds[3].getMax().y);
                path.lineTo(buttonBounds[3].getMin().x, buttonBounds[3].getMax().y);
                path.closePath();

                g2d.setColor(new Color(r, g, b, alpha));
                g2d.fill(path);

                g2d.setClip(old);
            }
        } else visible = JavaInputHandler.instance.isKeyDown(KeyEvent.VK_F1);
    }

    public void setContentString(int index, String string) {
        if (index < strings.size() && index >= 0) strings.set(index, string);
    }

    // public void setContentStrings(List<Entity> entities) {
    //     List<String> strings = new ArrayList<String>();
    //     for (Entity e : entities) strings.add(e.getID().toString());
    //     if (strings.size() * (fontSize * 0.8f) > realSize.y - titleMargin) realSize.y = strings.size() * (fontSize * 0.8f) + titleMargin;
    //     this.strings = strings;
    // }

    public G2DWindow addContentString(String string) {
        this.strings.add(string);
        return this;
    }

    public Vector2k getPosition() {
        return position;
    }

    public int getWidth() {
        return (int) visibleSize.x;
    }

    public int getHeight() {
        return (int) visibleSize.y;
    }

    public String getName() {
        return name;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
}
