package net.acidfrog.kronos.scene;

import java.util.HashMap;
import java.util.Map;
import java.awt.Graphics2D;

import net.acidfrog.kronos.core.lang.annotations.Debug;
import net.acidfrog.kronos.core.lang.assertions.Asserts;
import net.acidfrog.kronos.core.lang.logger.Logger;

public final class SceneManager {

    public static final SceneManager instance = new SceneManager();

    private Scene currentScene;
    private boolean sceneLoaded;
    private static Map<Integer, Scene> scenes = new HashMap<Integer, Scene>();
    
    private static int sceneIndex = 0;

    private SceneManager() {
        this.currentScene = null;
        this.sceneLoaded = false;
    }
    
    public void update(float dt) {
        Asserts.assertTrue(sceneLoaded, "No scene loaded!");
    
        if (currentScene != null) currentScene.update(dt);
        else sceneLoaded = false;
    }

    public void physicsUpdate(float pdt) {
        Asserts.assertTrue(sceneLoaded, "No scene loaded!");
        
        if (currentScene != null) currentScene.physicsUpdate(pdt);
        else sceneLoaded = false;
    }

    @Debug
    public void render(Graphics2D g2d) {
        Asserts.assertTrue(sceneLoaded, "No scene loaded!");
        
        if (currentScene != null) currentScene.render(g2d);
        else sceneLoaded = false;
    }
    
    public void close() {
        if (currentScene != null) currentScene.close();
    }

    public void loadScene(Scene scene) {
        if (currentScene != null) currentScene.close();
        if (!scenes.containsValue(scene)) addScene(scene);
        currentScene = scene;
        sceneLoaded = true;
    }

    public boolean nextScene() {
        int i = currentScene.getIndex() + 1;
        
        if (i < scenes.size()) {
            if (currentScene != null) currentScene.close();
            loadScene(scenes.get(i));
            return true;
        }
        return false;
    }

    public boolean prevScene() {
        int i = currentScene.getIndex() - 1;
        
        if (i >= 0) {
            if (currentScene != null) currentScene.close();
            loadScene(scenes.get(i));
            return true;
        }
        return false;
    }

    public Scene getSceneByIndex(int index) {
        for (Map.Entry<Integer, Scene> entry : scenes.entrySet()) if (entry.getKey() == index) return entry.getValue();
        Logger.instance.logError("No scene found with index " + index);
        return null;
    }

    public Scene getSceneByName(String name) {
        for (Map.Entry<Integer, Scene> entry : scenes.entrySet()) if (entry.getValue().getName().equals(name)) return entry.getValue();
        Logger.instance.logError("No scene found with name " + name);
        return null;
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public void addScene(Scene scene) {
        scenes.put(scene.setIndex(sceneIndex++), scene);
        loadScene(scene);
    }

    public void removeScene(Scene scene) {
        scene.close();
        
        if (scene == currentScene) {
            currentScene = null;
            sceneLoaded = false;
        }

        scenes.remove(scene.getIndex());
    }

    public void removeSceneByIndex(int index) {
        Scene scene = getSceneByIndex(index);
        if (scene != null) removeScene(scene);
    }

    public void removeSceneByName(String name) {
        Scene scene = getSceneByName(name);
        if (scene != null) removeScene(scene);
    }
    
}
