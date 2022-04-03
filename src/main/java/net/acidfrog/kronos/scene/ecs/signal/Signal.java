package net.acidfrog.kronos.scene.ecs.signal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A signal is a basic type that can dispatch an event to multiple listeners. It uses generics to allow any type
 * of object to be dispatched.
 * 
 * @since 0.0.2
 * @version 0.0.2
 * @author Ethan Temprovich
 */
public class Signal<T> {

    /** Holds all listeners registered to this signal */
    private List<SignalListener<T>> listeners = new CopyOnWriteArrayList<SignalListener<T>>();
    
    /**
     * Regsiters a {@link SignalListener listener} to this signal.
     * 
     * @param listener
     */
    public void register(SignalListener<T> listener) {
        if (listeners.contains(listener)) return;
        listeners.add(listener);
    }
    
    /**
     * Unregisters a {@link SignalListener listener} from this signal.
     * 
     * @param listener
     */
    public void unregister(SignalListener<T> listener) {
        listeners.remove(listener);
    }
    
    /**
     * Dispatches an event to all registered {@link SignalListener listeners}.
     * 
     * @param data
     */
    public void dispatch(T data) {
        for (SignalListener<T> listener : listeners) listener.receive(data);
    }
    
}