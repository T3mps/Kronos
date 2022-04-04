package net.acidfrog.kronos.scene;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;

import net.acidfrog.kronos.core.lang.Std;
import net.acidfrog.kronos.math.Vector2f;
import net.acidfrog.kronos.math.Vector4f;
import net.acidfrog.kronos.rendering.Camera;
import net.acidfrog.kronos.rendering.Renderer2D;
import net.acidfrog.kronos.rendering.Shader;
import net.acidfrog.kronos.rendering.Texture;
import net.acidfrog.kronos.rendering.Viewport;
import net.acidfrog.kronos.rendering.component.SpriteRendererComponent;
import net.acidfrog.kronos.scene.component.TagComponent;
import net.acidfrog.kronos.scene.component.TransformComponent;
import net.acidfrog.kronos.scene.ecs.Entity;
import net.acidfrog.kronos.scene.ecs.EntityListener;
import net.acidfrog.kronos.scene.ecs.Registry;
import net.acidfrog.kronos.scene.ecs.component.Component;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;


public class Scene {

    protected String name;
    protected int index;
    
    protected Registry registry;
    protected Map<String, Entity> entities;
    
    protected Renderer2D renderer;
    protected Camera camera;

    protected boolean loaded;
    
    public Scene(int index) {
        this(Std.Strings.Generator.randomName(), index);
    }

    public Scene(String name, int index) {
        this.name = name;
        this.index = index;
        this.registry = new Registry();
        this.entities = new HashMap<String, Entity>();
        this.renderer = new Renderer2D();
        this.camera = new Camera(Viewport.DEFAULT);
        this.loaded = false;
        testInit();
    }

    public void testInit() {
        int xOff = 10;
        int yOff = 10;

        float w = (float)(600 - xOff * 2);
        float h = (float)(600 - yOff * 2);

        float sx = w / 100f;
        float sy = h / 100f;

        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                float xx = xOff + sx * x;
                float yy = yOff + sy * y;

                Entity e = new Entity();
                e.add(new TransformComponent(new Vector2f(xx, yy), 0f, new Vector2f(sx, sy)));
                e.add(new SpriteRendererComponent(new Vector4f(xx / w, yy / h, 1, 1)));

                addEntity(e);
            }
        }
    }

    public void update(float dt) {
        registry.update(dt);
    }
    
    public void render() {
        renderer.render();
    }

    void close() {
        registry.dispose();
    }

    public void addEntity(Entity entity) {
        validate(entity);
        entities.put(entity.get(TagComponent.class).getName(), entity);
        registry.add(entity);
        renderer.add(entity);
    }

    void validate(Entity entity) {
        if (!entity.has(TagComponent.class)) entity.add(new TagComponent(Std.Strings.Generator.randomName()));
        if (!entity.has(TransformComponent.class)) entity.add(new TransformComponent());
    }

    public Entity getEntity(String name) {
        return entities.get(name);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity.get(TagComponent.class).getName());
        registry.destroy(entity);
    }

    public void registerListener(EntityListener listener) {
        registry.register(listener);
    }

    public void unregisterListener(EntityListener listener) {
        registry.unregister(listener);
    }

    public Registry getRegistry() {
        return registry;
    }

    public Camera getCamera() {
        return camera;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
    
}
