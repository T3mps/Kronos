package com.starworks.kronos.scene;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public final class SceneManager {

	private static SceneManager s_instance = null;
	
	private Scene m_currentScene;
	private final List<Reference<Scene>> m_previousScenes;
	
	private SceneManager() {
		this.m_currentScene = new Scene("unnamed");
		this.m_previousScenes = new ArrayList<Reference<Scene>>();
	}

	public void safeLoad(String path) {
		// TODO: serialize and save current scene if possible, then load new scene from file
		save("");
		load(path);
	}
	
	public void load(String path) {
		// TODO: implement loading of scenes from json file
		var prev = new SoftReference<Scene>(m_currentScene);
		if (!m_previousScenes.contains(prev)) {
			m_previousScenes.add(prev);
		}
		m_currentScene = deserialize(path);
	}
	
	public void save(String path) {
		// TODO: implement saving of scenes to existing or generated json file
		serialize();
	}
	
	private void serialize() {
		// serialize current scene
	}
	
	private Scene deserialize(String path) {
		// deserialize scene from file and return new scene
		// if the path points to a previous scene, and the reference is valid, load from reference
		int size = m_previousScenes.size();
		for (int i = 0; i < size; i++) {
			var ref = m_previousScenes.get(i);
			if (ref.get() == null) {
				m_previousScenes.remove(i);
			}
		}
		return null;
	}
	
	public Scene getCurrentScene() {
		return m_currentScene;
	}
	
	public static SceneManager get() {
		if (s_instance == null) {
			synchronized (SceneManager.class) {
				if (s_instance == null) {
					s_instance = new SceneManager();
				}
			}
		}
		return s_instance;
	}
}
