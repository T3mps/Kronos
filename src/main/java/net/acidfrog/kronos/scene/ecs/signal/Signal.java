package net.acidfrog.kronos.scene.ecs.signal;

import java.util.ArrayList;
import java.util.List;

public class Signal<T> {

    private List<Listener<T>> listeners = new ArrayList<Listener<T>>();

    public Signal() {}

    public void add(Listener<T> listener) {
        listeners.add(listener);
    }

    public void remove(Listener<T> listener) {
        listeners.remove(listener);
    }

    public void removeAll() {
        listeners.clear();
    }

    public void dispatch(T data) {
        for (Listener<T> listener : listeners) listener.receive(this, data);
    }
    
}
