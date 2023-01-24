package com.starworks.kronos.event;

import java.util.function.Consumer;

@FunctionalInterface
public interface EventCallback<E extends Event> extends Consumer<E> {

	void accept(E event);
}
