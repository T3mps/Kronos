package com.starworks.kronos.ecs.scripting.lua;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import com.starworks.kronos.ecs.Entity;
import com.starworks.kronos.ecs.scripting.Script;
import com.starworks.kronos.files.FileHandle;
import com.starworks.kronos.files.FileSystem;

public final class LuaScript implements Script {

	private final LuaValue m_chunk;
	private final LuaValue m_initializeFunction;
	private final LuaValue m_updateFunction;

	private LuaScript(LuaEngine engine, String fileName) {
		FileHandle handle = null;
		try {
			handle = FileSystem.getFileHandle(fileName, false, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			this.m_chunk = engine.getGlobals().load(new FileInputStream(handle.getFile()), fileName.substring(fileName.lastIndexOf("/") + 1), "t", engine.getGlobals());
			this.m_chunk.call();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error: Lua script '" + fileName + "' was unable to load");
		}

		this.m_initializeFunction = engine.getGlobals().get("initialize");
		this.m_updateFunction = engine.getGlobals().get("update");
		
		if (m_initializeFunction.isnil() || m_updateFunction.isnil()) {
			throw new RuntimeException("Error: Lua script '" + fileName + "' must have both 'initialize' and 'update' functions");
		}
	}
	
	public static LuaScript attach(LuaEngine engine, Entity entity, String fileName) {
		LuaScript script = new LuaScript(engine, FileSystem.get(fileName));
		entity.add(script);
		script.m_initializeFunction.call(CoerceJavaToLua.coerce(entity));
		return script;
	}

	public LuaValue initializeFunction() {
		return m_initializeFunction;
	}

	public LuaValue updateFunction() {
		return m_updateFunction;
	}

	public LuaValue getChunk() {
		return m_chunk;
	}
}
