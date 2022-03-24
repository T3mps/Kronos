package net.acidfrog.kronos.scene.ecs.signal;

public interface Listener<T> {

    public void receive(Signal<T> signal, T data);
    
}
