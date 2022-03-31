package net.acidfrog.kronos.scene.ecs.signal;

public interface SignalListener<T> {

    public void receive(T data);
    
}
