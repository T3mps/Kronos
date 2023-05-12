package com.starworks.kronos.ecs.scripting.lua;

import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.starworks.kronos.ecs.scripting.ScriptEngine;
import com.starworks.kronos.ecs.scripting.ScriptModule;

public interface LuaModule extends ScriptModule {

	@Override
	public <L extends ScriptEngine> void install(L luaEngine);

	public default void register(String functionName, ZeroArgFunction function) {
		getEngine().getGlobals().set(functionName, function);
	}

	public default void register(String functionName, OneArgFunction function) {
		getEngine().getGlobals().set(functionName, function);
	}

	public default void register(String functionName, TwoArgFunction function) {
		getEngine().getGlobals().set(functionName, function);
	}

	public default void register(String functionName, ThreeArgFunction function) {
		getEngine().getGlobals().set(functionName, function);
	}

	public default void register(String functionName, VarArgFunction function) {
		getEngine().getGlobals().set(functionName, function);
	}

	public LuaEngine getEngine();
}
