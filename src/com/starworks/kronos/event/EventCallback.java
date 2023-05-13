package com.starworks.kronos.event;

@FunctionalInterface
public interface EventCallback<E extends Event> {
	
    int DEFAULT_PRIORITY = 0;

    boolean accept(E value);
    
    default int getPriority() {
        return DEFAULT_PRIORITY;
    }
}
