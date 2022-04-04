package net.acidfrog.kronos.scene;

import java.util.HashMap;
import java.util.Map;

public final class SceneManager {

    private static SceneManager instance;

    private int nextIndex = -1;
    private int currentScene = -1;

    private Map<Integer, Scene> sceneByIndex = new HashMap<Integer, Scene>();
    private Map<String, Scene> sceneByName = new HashMap<String, Scene>();

    private SceneManager(Scene... scenes) {
        for (Scene scene : scenes) {
            sceneByIndex.put(nextIndex, scene);
            sceneByName.put(scene.getName(), scene);
            nextIndex++;
        }

        if (scenes.length > 0) currentScene = 0;
    }

    public static SceneManager getInstance() {
        if (instance == null) instance = new SceneManager();
        return instance;
    }

    private Scene cache = null;

    public void update(float dt) {
        if (currentScene == -1) return;
        if (cache == null) cache = sceneByIndex.get(currentScene);
        cache.update(dt);
    }

    public void render() {
        if (currentScene == -1) return;
        if (cache == null) cache = sceneByIndex.get(currentScene);
        cache.render();
    }

    public Scene addScene(Scene scene) {
        sceneByIndex.put(++nextIndex, scene);
        sceneByName.put(scene.getName(), scene);
        if (currentScene == -1) currentScene = nextIndex;
        return scene;
    }

    public Scene nextScene() {
        if (currentScene == -1) return null;

        sceneByIndex.get(currentScene).close();

        if (currentScene + 1 < sceneByIndex.size()) currentScene++;
        else currentScene = 0;

        return sceneByIndex.get(currentScene);
    }

    public Scene previousScene() {
        if (currentScene == -1) return null;

        sceneByIndex.get(currentScene).close();

        if (currentScene - 1 >= 0) currentScene--;
        else currentScene = sceneByIndex.size() - 1;

        return sceneByIndex.get(currentScene);
    }

    public Scene getScene(int index) {
        return sceneByIndex.get(index);
    }

    public Scene getScene(String name) {
        return sceneByName.get(name);
    }

    public Scene getCurrentScene() {
        return currentScene == -1 ? null : sceneByIndex.get(currentScene);
    }

    public Scene removeScene(int index) {
        Scene scene = sceneByIndex.remove(index);
        sceneByName.remove(scene.getName());
        return scene;
    }

    public Scene removeScene(String name) {
        Scene scene = sceneByName.remove(name);
        sceneByIndex.remove(scene.getIndex());
        return scene;
    }

    public void clear() {
        sceneByIndex.clear();
        sceneByName.clear();
        nextIndex = -1;
        currentScene = -1;
    }

}
