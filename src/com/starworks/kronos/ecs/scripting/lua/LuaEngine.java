package com.starworks.kronos.ecs.scripting.lua;

import java.io.IOException;
import java.util.List;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import com.starworks.kronos.ecs.Entity;
import com.starworks.kronos.ecs.Registry;
import com.starworks.kronos.ecs.Scheduler;
import com.starworks.kronos.ecs.scripting.ScriptEngine;
import com.starworks.kronos.logging.Logger;
import com.starworks.kronos.toolkit.Reflections;

public final class LuaEngine implements ScriptEngine {
	private final Logger LOGGER = Logger.getLogger(LuaEngine.class);

	private final Registry m_registry;
	private final Globals m_globals;

	public LuaEngine(Registry registry) {
		this.m_registry = registry;
		this.m_globals = JsePlatform.standardGlobals();
	}

	public LuaEngine bind(Scheduler scheduler, String... packages) {
		LoadState.install(m_globals);
		LuaC.install(m_globals);
		LuaKronosModule.get().install(this);
		install(packages);
		
		scheduler.submit(this);
		LOGGER.info("LuaEngine bound to registry");
		return this;
	}

	@Override
	public void run() {
		m_registry.view(LuaScript.class).stream().forEach(view -> {
			Entity entity = view.entity();
			LuaScript component = view.component();
			component.updateFunction().call(CoerceJavaToLua.coerce(entity));
		});
	}
	
	private void install(String pkg) {
		try {
			List<Class<?>> componentClasses = Reflections.findClassesWithPattern(pkg, "Component");
            for (var componentClass : componentClasses) {
            	String simpleName = componentClass.getSimpleName();
                String snakeCaseName = camelCaseToSnakeCase(simpleName);
            	m_globals.set(snakeCaseName, CoerceJavaToLua.coerce(componentClass));
            }
		} catch (ClassNotFoundException | IOException e) {
			throw new RuntimeException("Failed to register component classes in Lua", e);
		}
	}
	
	private void install(String[] packages) {
		for (var pkg : packages) {
			install(pkg);
		}
	}
	
	private static String camelCaseToSnakeCase(String camelCase) {
        StringBuilder sb = new StringBuilder();
        for (char c : camelCase.toCharArray()) {
            if (Character.isUpperCase(c)) {
                if (sb.length() > 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

	public Registry getRegistry() {
		return m_registry;
	}

	public Globals getGlobals() {
		return m_globals;
	}
}
