package com.starworks.kronos.ecs.scripting;

@FunctionalInterface
public interface ScriptModule {

	public <E extends ScriptEngine> void install(E engine);
}
