package com.starworks.kronos.api.scene;

import com.starworks.kronos.inferno.Entity;

public class ScriptableGameObject extends GameObject {

    public ScriptableGameObject(Entity entity, Scene scene) {
        super(entity, scene);
    }
    
    public ScriptableGameObject(ScriptableGameObject other) {
        super(other);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ScriptableGameObject{");
        sb.append("entity=").append(entity);
        sb.append(", scene=").append(scene);
        sb.append('}');
        return sb.toString();
    }
}
