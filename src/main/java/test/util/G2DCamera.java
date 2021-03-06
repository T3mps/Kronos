package test.util;

import java.awt.event.KeyEvent;

import net.acidfrog.kronos.math.Vector2k;

public class G2DCamera {

    public static final G2DCamera instance = new G2DCamera();

    private Vector2k position;
    private float zoomLevel = 1.0f;
    private float zoomScale = 0.05f;

    private static final float MAX_ZOOM = 2.5f;
    private static final float MIN_ZOOM = 0.05f;
    private static final float PAN_SPEED = 10f;
    
    private G2DCamera() {
      this.position = new Vector2k(0, 0);
    }

    public void update(float dt) {
      float delta = (PAN_SPEED + dt) * (zoomLevel);
      if (delta <= PAN_SPEED * 0.75f) delta = PAN_SPEED * 0.75f;

      if (JavaInputHandler.instance.isKey(KeyEvent.VK_W)) position.y += delta;
      if (JavaInputHandler.instance.isKey(KeyEvent.VK_S)) position.y -= delta;
      if (JavaInputHandler.instance.isKey(KeyEvent.VK_A)) position.x += delta;
      if (JavaInputHandler.instance.isKey(KeyEvent.VK_D)) position.x -= delta;

      if (JavaInputHandler.instance.isKey(KeyEvent.VK_Q)      && zoomLevel > MIN_ZOOM) zoomOut();
      else if (JavaInputHandler.instance.isKey(KeyEvent.VK_E) && zoomLevel < MAX_ZOOM) zoomIn();

      if (JavaInputHandler.instance.isKey(KeyEvent.VK_SPACE)) { originalSize(); position.zero(); }
    }

    public final Vector2k toWorldCoordinates(float width, float height, Vector2k p) {
      if (p != null) {
        Vector2k v = new Vector2k();
        // convert the screen space point to world space
        v.x =  (p.x - width  * 0.5f - position.x) / zoomLevel;
        v.y = -(p.y - height * 0.5f + position.y) / zoomLevel;
        return v;
      }
      
      return null;
    }

    public void setZoomScale(float zoomScale) {
      this.zoomScale = (zoomScale / 100f);
    }
  
    public void originalSize() {
      zoomLevel = 1f;
    }

    public void zoomIn() {
      if (zoomLevel < MAX_ZOOM) zoomLevel += zoomLevel * zoomScale;
    }
  
    public void zoomOut() {
      if (zoomLevel > MIN_ZOOM) zoomLevel -= zoomLevel * zoomScale;
    }

    public Vector2k getPosition() {
        return position;
    }

    public float getZoomLevel() {
        return zoomLevel;
    }

}
