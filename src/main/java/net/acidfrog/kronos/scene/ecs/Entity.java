package net.acidfrog.kronos.scene.ecs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.acidfrog.kronos.core.lang.IDArbiter;
import net.acidfrog.kronos.core.lang.Std;
import net.acidfrog.kronos.core.lang.UUID;
import net.acidfrog.kronos.core.lang.logger.Logger;
import net.acidfrog.kronos.scene.SceneManager;
import net.acidfrog.kronos.scene.ecs.component.Component;

public class Entity implements Comparable<Entity> {

	protected Set<Component> components;
	protected Iterator<Component> componentIterator;
	
	protected Entity parent;
	protected List<Entity> children;
	
	protected UUID id;
	protected int index;
    protected boolean enabled;
	protected boolean simulated;

	/**
	 * Creates a new {@link Entity} with the given components.
	 */
	public Entity(Component... components) {
		this.components = new HashSet<Component>();
		this.componentIterator = this.components.iterator();
		this.parent = null;
		this.children = new ArrayList<Entity>();
		this.id = UUID.generate();
		this.index = IDArbiter.next();
		this.addComponent(components);
		this.simulated = false;
		this.enabled = false;
	}

	/**
	 * Creates a new, empty {@link Entity}.
	 */
	public Entity() {
		this.components = new HashSet<Component>();
		this.componentIterator = components.iterator();
		this.parent = null;
		this.children = new ArrayList<Entity>();
		this.id = UUID.generate();
		this.index = IDArbiter.next();
		this.simulated = false;
		this.enabled = false;
	}

	/**
	 * Called when the Entity is added to the {@link net.acidfrog.kronos.scene.Scene}.
	 */
	public void onEnable() {
		for (Component component : components) component.enable();
	}

	/**
	 * Called during the regular update step.
	 * 
	 * @param dt
	 */
	public void update(float dt) {
		for (Component c : components) if (c.isEnabled()) c.update(dt);
	}

	/**
	 * Called during the physics update step.
	 * 
	 * @param pdt
	 */
	public void physicsUpdate(float pdt) {
		for (Component c : components) if (c.isEnabled()) c.physicsUpdate(pdt);
	}

	/**
	 * Called when this Entity is disabled.
	 */
	public void onDisable() {
		for (Component component : components) component.disable();
	}

	/**
	 * Checks weather or not this {@link Entity} contains a component of the given type.
	 * 
	 * @param <T>
	 * @param c
	 * @return instanceof <T> component if found, null otherwise
	 */
	public <T extends Component> T getComponent(Class<T> c) {
		componentIterator = components.iterator();

		while (componentIterator.hasNext()) {
			var com = componentIterator.next();
			if (com.getClass() == c) return c.cast(com);
		}

		Logger.instance.logWarn("Component of type " + c.getName() + " not found in Entity " + id);
		return null;
	}

	/**
	 * Checks weather or not this {@link Entity} contains a component by ID.
	 * 
	 * @param id
	 * @return specific component if found, null otherwise
	 */
	public Component getComponentByID(UUID id) {
		componentIterator = components.iterator();

		while (componentIterator.hasNext()) {
			var com = componentIterator.next();
			if (com.getID().equals(id)) return com;
		}

		Logger.instance.logWarn("Component with ID " + id + " not found in Entity " + id);
		return null;
	}

	/**
	 * Generic check for weather or not this {@link Entity} contains a component of type T.
	 * 
	 * @param <T>
	 * @param c
	 * @return true if this entity contains a component of type T, false otherwise
	 */
	public <T extends Component> boolean hasComponent(Class<T> c) {
		return getComponent(c) != null;
	}

	/**
	 * Checks if this {@link Entity} contains a specific component.
	 * 
	 * @param c
	 * @return true if this entity contains the component, false otherwise
	 */
	public boolean hasComponent(Component c) {
		return components.contains(c);
	}

	/**
	 * Adds n components to this {@link Entity} and sets their parent to this {@link Entity}.
	 * 
	 * @param components
	 * @return array of components present on this entity after adding n components
	 */
	public Component[] addComponent(Component... components) {
		return addComponentsFromCollection(Set.of(components));
	}

	/**
	 * Adds all components from an {@link Entity} to this Entity.
	 * 
	 * @param entity
	 * @return
	 */
	public Component[] addComponentsFromEntity(Entity e) {
		return addComponentsFromCollection(e.components);
	}

	/**
	 * Adds all components from any Collection to this {@link Entity}.
	 * 
	 * @param components
	 * @return array of components present on this entity after adding n components
	 */
	public <T extends Component> Component[] addComponentsFromCollection(Collection<T> components) {
		if (components.size() > 0) {
			int iter = 0;
			for (Component c : components) {
				if (this.components.contains(c)) {
					Logger.instance.logWarn("Component " + c.getClass().getSimpleName() + " already present on entity $" + this.id);
					continue;
				} else iter++;
	
				c.setParent(this);
				this.components.add(c);
			}
			if (iter == 0)  {
				Logger.instance.logWarn("No components to add to entity $" + this.id);
				return new Component[0];
			}
		}

		return components.toArray(new Component[components.size()]);
	}

	/**
	 * Removes n components from this {@link Entity}, calling their onRemove() method.
	 * 
	 * @param components
	 * @return this
	 */
	public Entity removeComponent(Component... component) {
		for (Component c : component) {
			if (hasComponent(c)) {
				c.setParent(null);
				c.disable();
				components.remove(c);
			} else {
				Logger.instance.logWarn("Tried to remove component $" + c.getID() + " from entity $" + this.id + " but it was not found.");
				continue;
			}
		}
		return this;
	}

	/**
	 * Removes a components referenced by ID from this {@link Entity}, calling their onRemove() method.
	 * 
	 * @param id
	 * @return this
	 */
	public Entity removeComponentByID(UUID id) {
		var c = getComponentByID(id);
		if (c != null) removeComponent(c);
		else Logger.instance.logWarn("Tried to remove component $" + id + " from entity $" + this.id + " but it was not found.");
		return this;
	}

	/**
	 * Removes all components from this {@link Entity}, calling their onRemove() method.
	 * 
	 * @return this
	 */
	public Entity removeAllComponents() {
		for (Component c : components) {
			c.setParent(null);
			c.disable();
			components.remove(c);
		}

		components.clear();

		return this;
	}

	/**
	 * Returns this {@link Entity}'s parent {@link Entity}.
	 * 
	 * @return parent
	 */
	public Entity getParent() {
		return parent;
	}

	/**
	 * Ensures a valid parent Entity and sets this {@link Entity}'s parent to it; also
	 * adds this {@link Entity} to the parent's children list if not already contained.
	 * 
	 * @param parent
	 * @return this
	 */
	public Entity setParent(Entity parent) {
		if (hasChild(parent)) {
			Logger.instance.logError("Tried to set Entity $" + this.id + "s parent to Entity $" + parent.getID() + ", which is a child of $" + this.id + ".");
			return this;
		}
		if (parent != null && parent != null && parent != this) {
			if (this.parent != null && this.parent != null && this.parent.hasChild(this)) this.parent.removeChild(this);	
			if (!parent.hasChild(this)) parent.addChild(this);
		}
		this.parent = parent;
		return this;
	}

	/**
	 * Returns the 
	 * 
	 * @return {@link Entity}
	 */
	public Entity getChild(Entity e) {
		for (Entity child : children) if (child == e) return child;
		return null;
	}

	public Entity getChildByIndex(int index) {
		if (index < 0 || index >= children.size()) {
			Logger.instance.logError(index + " out of bounds of children list on entity $" + this.id);
			return null;
		}
		return children.get(index);
	}

	public Entity getChildByID(UUID id) {
		for (Entity child : children) if (child.getID().equals(id)) return child;
		Logger.instance.logWarn("Entity $" + id + " not a child of entity $" + this.id);
		return null;
	}

	public List<Entity> getChildren() {
		return children;
	}

	public boolean hasChild(Entity e) {
		return children.size() > 0 && children.contains(e) && e != this;
	}

	/**
	 * Determines if the {@link Entity}ies are valid children and adds them to the list
	 * of children.
	 * 
	 * @param child
	 * @return this
	 */
	@SuppressWarnings("null")
	public Entity addChild(Entity... child) {
		Entity inc = null; // c will be set in the lower two lines if possible, otherwise it will be null
		boolean childIsThis = Std.Arrays.contains(child, this, inc);
		boolean childIsParent = Std.Arrays.contains(child, parent,inc);

		if (child.length == 0 || child == null || (child.length == 1 && (child[0] == null || child[0] == null))) {
			Logger.instance.logWarn("Tried to add a child to entity $" + this.id + " but no child was specified.");
			return this;
		}
		if (childIsThis || childIsParent) {
			Logger.instance.logError("Tried to give Entity $" + this.id + " a child Entity $" + inc.id + ", but it is " + (childIsThis ? "itself" : "its parent") + ".");
			return this;
		}
		for (Entity e : child) {
			if (e == null || e == this) continue;
			if (e.getParent() != null && e.getParent() != null) e.getParent().removeChild(e);
			e.setParent(this);
			children.add(e);
		}
		return this;
	}

	/**
	 * Adds a child {@link Entity} to this {@link Entity} from the current scene
	 * referenced by its ID.
	 * 
	 * @param id
	 * @return child Entity
	 */
	public Entity addChildByIDFromScene(UUID id) {
		return addChild(SceneManager.instance.getCurrentScene().getEntityByID(id));
	}

	/**
	 * Adds all of the children from a specified entity to this Entity.
	 * 
	 * @param e
	 * @return this
	 */
	public Entity addChildrenFromEntity(Entity e) {
		return addChild(e.getChildren().toArray(new Entity[0]));
	}
	
	/**
	 * Removes a child {@link Entity} from this {@link Entity}.
	 * 
	 * @param child
	 */
	public void removeChild(Entity child) {
		if (this.hasChild(child)) {
			child.setParent(null);
			children.remove(child);
		} else Logger.instance.logWarn("Tried to remove child $" + id + " from entity $" + this.id + " but it was not found.");
	}

	/**
	 * Removes a child Entity from this {@link Entity}, referenced by its ID.
	 * 
	 * @param id of child Entity
	 * @return this
	 */
	public Entity removeChildByID(UUID id) {
		removeChild(getChildByID(id));
		return this;
	}

	/**
	 * Removes all children from this {@link Entity}.
	 * 
	 * @return this
	 */
	public Entity removeChildren() {
		for (Entity child : children) removeChild(child);
		children.clear();
		return this;
	}

	/**
	 * Returns this {@link Entity}'s ID.
	 * 
	 * @return id
	 */
	public UUID getID() {
		return id;
	}

	/**
	 * Returns this {@link Entity}'s index.
	 * 
	 * @return index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns weather or not this {@link Entity} is enabled.
	 * 
	 * @return boolean
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Enables this {@link Entity}, calling its onEnable() method.
	 * 
	 * @return this
	 */
	public Entity enable() {
		if (!enabled) {
			enabled = true;
			onEnable();
		}
		return this;
	}

	/**
	 * Disables this {@link Entity}, calling its onDisable() method.
	 * 
	 * @return this
	 */
	public Entity disable(boolean callback) {
		if (enabled) {
			enabled = false;
			if (callback) onDisable();
		}
		return this;
	}

	/**
	 * Returns weather or not this {@link Entity} should be simulaed in the {@link net.acidfrog.kronos.physics.world.PhysicsWorld}.
	 * 
	 * @return boolean
	 */
	public boolean isSimulated() {
		return simulated;
	}

	/**
	 * Allow or disallow this {@link Entity}'s simulation {@link net.acidfrog.kronos.physics.world.PhysicsWorld}.
	 * 
	 * @param simulated
	 * @return this
	 */
	public Entity setSimulated(boolean simulated) {
		this.simulated = simulated;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((componentIterator == null) ? 0 : componentIterator.hashCode());
		result = prime * result + ((components == null) ? 0 : components.hashCode());
		result = prime * result + (enabled ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + (simulated ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Entity)) return false;
		Entity other = (Entity) obj;
		if (children == null) {
			if (other.children != null) return false;
		} else if (!children.equals(other.children)) return false;
		if (components == null) {
			if (other.components != null) return false;
		} else if (!components.equals(other.components)) return false;
		if (enabled != other.enabled) return false;
		if (id == null) {
			if (other.id != null) return false;
		} else if (!id.equals(other.id)) return false;
		if (parent == null) {
			if (other.parent != null) return false;
		} else if (!parent.equals(other.parent)) return false;
		if (simulated != other.simulated) return false;
		return true;
	}

	/**
	 * Compares this {@link Entity} to another {@link Entity} by their UUID,
	 * if present. If not, compares by their index. The UUID test if preffered.
	 * 
	 * @param o the object to compare this {@link Entity} to
	 * @return the result of the comparison
	 */
	@Override
	public int compareTo(Entity o) {
		if (id != null && o.id != null) return id.compareTo(o.id);
		return Integer.compare(index, o.index);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Entity [children=");
		builder.append(children);
		builder.append(", componentIterator=");
		builder.append(componentIterator);
		builder.append(", components=");
		builder.append(components);
		builder.append(", enabled=");
		builder.append(enabled);
		builder.append("]");
		return builder.toString();
	}

}
