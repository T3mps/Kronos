package com.starworks.kronos.api.scene;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import com.starworks.kronos.api.scene.component.IDComponent;
import com.starworks.kronos.api.scene.component.TagComponent;
import com.starworks.kronos.inferno.Entity;

public class GameObject {

    protected final Entity entity;
    protected Reference<Scene> scene;
    
    public GameObject(Entity entity, Scene scene) {
        this.entity = entity;
        this.scene = new WeakReference<Scene>(scene);
    }

    public GameObject(GameObject other) {
        this(other.entity, other.scene.get());
    }

    public void addComponent(final Object component) { entity.add(component); }

    public void removeComponent(final Object component) { entity.remove(component); }

    public boolean hasComponent(final Class<?> componentClass) { return entity.has(componentClass); }
    
    public boolean hasComponent(final Object component) { return entity.contains(component); }
    
    public Object getComponent(final Class<?> componentClass) { return entity.get(componentClass); }

    public TagComponent getTag() { return (TagComponent) entity.get(TagComponent.class); }

    public IDComponent getUUID() { return (IDComponent) entity.get(IDComponent.class); }

    public Scene getScene() { return scene.get(); }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GameObject other = (GameObject) obj;
        if (this.entity != other.entity && (this.entity == null || !this.entity.equals(other.entity))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GameObject{");
        sb.append("entity=").append(entity);
        sb.append(", scene=").append(scene);
        sb.append('}');
        return sb.toString();
    }
}
