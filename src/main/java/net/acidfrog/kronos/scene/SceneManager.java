package net.acidfrog.kronos.scene;

import java.util.HashMap;
import java.util.Map;

import net.acidfrog.kronos.core.util.IDArbiter;

public final class SceneManager {

    private static SceneManager instance;

    private IDArbiter idArbiter;
    private int currentScene = -1;

    private Map<Integer, Scene> sceneByIndex = new HashMap<Integer, Scene>();
    private Map<String,  Scene> sceneByName  = new HashMap<String,  Scene>();

    private SceneManager(Scene... scenes) {
        this.idArbiter = new IDArbiter();

        for (Scene scene : scenes) {
            sceneByIndex.put(idArbiter.next(), scene);
            sceneByName.put(scene.getName(), scene);
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
        int index = idArbiter.next();
        sceneByIndex.put(index, scene);
        sceneByName.put(scene.getName(), scene);
        if (currentScene == -1) currentScene = index;
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
        idArbiter.reset();
        currentScene = -1;
    }

}
