package com.starworks.kronos.input.action;

public interface ActionCallback<T> {

	public void accept(T value);
}
