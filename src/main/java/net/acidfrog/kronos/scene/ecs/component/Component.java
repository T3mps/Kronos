package net.acidfrog.kronos.scene.ecs.component;

import net.acidfrog.kronos.core.lang.UUID;
import net.acidfrog.kronos.scene.ecs.Entity;

public abstract class Component implements Comparable<Component> {	

    protected Entity parent;
	protected UUID id;
	protected boolean enabled;

	public Component() {
		this.parent = null;
		this.id = UUID.generate();
		this.enabled = true;
	}
	
	protected void onEnable() {
	}

	public void update(float dt) {
	}

	public void physicsUpdate(float pdt) {
	}

	protected void onDisable() {
	}

	public Entity getParent() {
		return parent;
	}

	public void setParent(Entity parent) {
		this.parent = parent;
	}

	public void unparent() {
		this.parent = null;
	}

	public boolean isParented() {
		return !(parent == null);
	}

	public UUID getID() {
		return id;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void enable() {
		if (!enabled) {
			enabled = true;
			onEnable();
		}
	}

	public void disable() {
		if (enabled) {
			enabled = false;
			onDisable();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (enabled ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Component)) return false;
		Component other = (Component) obj;
		if (enabled != other.enabled) return false;
		if (id == null) {
			if (other.id != null) return false;
		} else if (!id.equals(other.id)) return false;
		if (parent == null) {
			if (other.parent != null) return false;
		} else if (!parent.equals(other.parent)) return false;
		return true;
	}

	@Override
	public int compareTo(Component c) {
		return (id.equals(c.getID())) ? +1 : -1;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Component [enabled=");
		builder.append(enabled);
		builder.append(", id=");
		builder.append(id);
		builder.append(", parent=");
		builder.append(parent);
		builder.append("]");
		return builder.toString();
	}
    
}
