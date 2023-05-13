package com.starworks.kronos.event;

public record PriorityEventCallback<E extends Event>(EventCallback<E> callback, int priority) {
	
	public static final int DEFAULT_PRIORITY = 0;
}
