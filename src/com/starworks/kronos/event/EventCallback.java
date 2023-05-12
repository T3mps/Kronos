package com.starworks.kronos.event;

@FunctionalInterface
public interface EventCallback<E extends Event> {

	boolean accept(E event);
}
