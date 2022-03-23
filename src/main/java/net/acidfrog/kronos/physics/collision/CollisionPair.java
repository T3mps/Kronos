package net.acidfrog.kronos.physics.collision;

/**
 * Represents a pair of objects that are colliding.
 * 
 * @author Ethan Temprovich
 */
public interface CollisionPair<T> {

    /**
     * @return the first object in the pair.
     */
    public T getA();

    /**
     * @return the second object in the pair.
     */
    public T getB();
    
}
