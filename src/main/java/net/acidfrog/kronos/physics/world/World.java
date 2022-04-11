package net.acidfrog.kronos.physics.world;

import java.util.List;

import net.acidfrog.kronos.physics.collision.broadphase.BroadphaseDetector;
import net.acidfrog.kronos.physics.collision.narrowphase.NarrowphaseDetector;
import net.acidfrog.kronos.physics.collision.narrowphase.RaycastDetector;
import net.acidfrog.kronos.physics.world.body.Body;

public interface World<T extends Body> {

    public void update(float dt);

    public void detect();

    public void add(T body);
    
    public T get(int index);

    public boolean remove(T body);

    public T remove(int index);
    
    public void removeAll();

    public int size();

    public List<T> getBodies();

    public BroadphaseDetector<T> getBroadphaseDetector();

    public NarrowphaseDetector getNarrowphaseDetector();

    public RaycastDetector getRaycastDetector();
    
}
