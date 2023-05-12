package com.starworks.kronos.ecs.scripting;

import com.starworks.kronos.ecs.Registry;

public interface ScriptEngine extends Runnable {

	public Registry getRegistry();
}
