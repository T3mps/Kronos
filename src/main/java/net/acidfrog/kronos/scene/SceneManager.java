package net.acidfrog.kronos.scene;

import java.util.HashMap;
import java.util.Map;

public final class SceneManager {

    private static int nextIndex = -1;
    private static int currentScene = -1;

    private static Map<Integer, Scene> sceneByIndex = new HashMap<Integer, Scene>();
    private static Map<String, Scene> sceneByName = new HashMap<String, Scene>();

    private SceneManager() {}

    public static Scene addScene(String name) {
        Scene scene = new Scene(name, ++nextIndex);
        sceneByIndex.put(nextIndex, scene);
        sceneByName.put(name, scene);

        if (currentScene == -1) currentScene = nextIndex;

        return scene;
    }

    public static Scene nextScene() {
        if (currentScene == -1) return null;

        sceneByIndex.get(currentScene).close();

        if (currentScene + 1 < sceneByIndex.size()) currentScene++;
        else currentScene = 0;

        return sceneByIndex.get(currentScene);
    }

    public static Scene previousScene() {
        if (currentScene == -1) return null;

        sceneByIndex.get(currentScene).close();

        if (currentScene - 1 >= 0) currentScene--;
        else currentScene = sceneByIndex.size() - 1;

        return sceneByIndex.get(currentScene);
    }

    public static Scene getScene(int index) {
        return sceneByIndex.get(index);
    }

    public static Scene getScene(String name) {
        return sceneByName.get(name);
    }

    public static Scene getCurrentScene() {
        return currentScene == -1 ? null : sceneByIndex.get(currentScene);
    }

    public static Scene removeScene(int index) {
        Scene scene = sceneByIndex.remove(index);
        sceneByName.remove(scene.getName());
        return scene;
    }

    public static Scene removeScene(String name) {
        Scene scene = sceneByName.remove(name);
        sceneByIndex.remove(scene.getIndex());
        return scene;
    }

    public static void clear() {
        sceneByIndex.clear();
        sceneByName.clear();
        nextIndex = -1;
        currentScene = -1;
    }

}
