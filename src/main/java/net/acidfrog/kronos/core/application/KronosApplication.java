package net.acidfrog.kronos.core.application;

import net.acidfrog.kronos.scene.SceneManager;

public abstract class KronosApplication implements Runnable {

    private final Window window;

    public KronosApplication(int windowWidth, int windowHeight, String windowTitle) {
        EntryPoint.initilize();
        this.window = Window.create(windowWidth, windowHeight, windowTitle, this);
		window.initialize();
    }

    @Override
    public void run() { window.run(); }

    public void update(float deltaTime) {
        SceneManager.instance.update(deltaTime);
    }

    public void physicsUpdate(float physicsDeltaTime) {
        SceneManager.instance.physicsUpdate(physicsDeltaTime);
    }

    public void render() {
        SceneManager.instance.render();
    }

}
