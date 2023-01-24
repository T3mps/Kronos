package com.starworks.kronos.input;

import java.util.function.Consumer;

public interface InputCallback<T> extends Consumer<T> {

	void accept(T data);
}
