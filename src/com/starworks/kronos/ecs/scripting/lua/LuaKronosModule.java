package com.starworks.kronos.ecs.scripting.lua;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;

import com.starworks.kronos.ecs.Entity;
import com.starworks.kronos.ecs.Registry;
import com.starworks.kronos.ecs.scripting.ScriptEngine;
import com.starworks.kronos.toolkit.concurrent.ArrivalGate;

public final class LuaKronosModule implements LuaModule {

	private static final ArrivalGate s_gate = new ArrivalGate(1);
	private static final LuaKronosModule s_instance = new LuaKronosModule();
	
	private LuaEngine m_engine;

	private LuaKronosModule() {
		try {
			s_gate.arrive();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static LuaKronosModule get() {
		return s_instance;
	}
	
	@Override
	public <L extends ScriptEngine> void install(L luaEngine) {		
		this.m_engine = (LuaEngine) luaEngine;

		register("view1", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg0) {
				Registry registry = m_engine.getRegistry();
				var type = (Class<?>) CoerceLuaToJava.coerce(arg0, Class.class);
				return CoerceJavaToLua.coerce(registry.view(type));
			}
		});
		register("view2", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue arg0, LuaValue arg1) {
				Registry registry = m_engine.getRegistry();
				var type0 = (Class<?>) CoerceLuaToJava.coerce(arg0, Class.class);
				var type1 = (Class<?>) CoerceLuaToJava.coerce(arg1, Class.class);
				return CoerceJavaToLua.coerce(registry.view(type0, type1));
			}
		});
		register("view3", new ThreeArgFunction() {
			@Override
			public LuaValue call(LuaValue arg0, LuaValue arg1, LuaValue arg2) {
				Registry registry = m_engine.getRegistry();
				var type0 = (Class<?>) CoerceLuaToJava.coerce(arg0, Class.class);
				var type1 = (Class<?>) CoerceLuaToJava.coerce(arg1, Class.class);
				var type2 = (Class<?>) CoerceLuaToJava.coerce(arg2, Class.class);
				return CoerceJavaToLua.coerce(registry.view(type0, type1, type2));
			}
		});
		register("view4", new VarArgFunction() {
			@Override
			public LuaValue invoke(Varargs args) {
				Registry registry = m_engine.getRegistry();
				var type0 = (Class<?>) CoerceLuaToJava.coerce(args.arg(0), Class.class);
				var type1 = (Class<?>) CoerceLuaToJava.coerce(args.arg(1), Class.class);
				var type2 = (Class<?>) CoerceLuaToJava.coerce(args.arg(2), Class.class);
				var type3 = (Class<?>) CoerceLuaToJava.coerce(args.arg(3), Class.class);
				return CoerceJavaToLua.coerce(registry.view(type0, type1, type2, type3));
			}
		});
		register("view5", new VarArgFunction() {
			@Override
			public LuaValue invoke(Varargs args) {
				Registry registry = m_engine.getRegistry();
				var type0 = (Class<?>) CoerceLuaToJava.coerce(args.arg(0), Class.class);
				var type1 = (Class<?>) CoerceLuaToJava.coerce(args.arg(1), Class.class);
				var type2 = (Class<?>) CoerceLuaToJava.coerce(args.arg(2), Class.class);
				var type3 = (Class<?>) CoerceLuaToJava.coerce(args.arg(3), Class.class);
				var type4 = (Class<?>) CoerceLuaToJava.coerce(args.arg(4), Class.class);
				return CoerceJavaToLua.coerce(registry.view(type0, type1, type2, type3, type4));
			}
		});
		register("view6", new VarArgFunction() {
			@Override
			public LuaValue invoke(Varargs args) {
				Registry registry = m_engine.getRegistry();
				var type0 = (Class<?>) CoerceLuaToJava.coerce(args.arg(0), Class.class);
				var type1 = (Class<?>) CoerceLuaToJava.coerce(args.arg(1), Class.class);
				var type2 = (Class<?>) CoerceLuaToJava.coerce(args.arg(2), Class.class);
				var type3 = (Class<?>) CoerceLuaToJava.coerce(args.arg(3), Class.class);
				var type4 = (Class<?>) CoerceLuaToJava.coerce(args.arg(4), Class.class);
				var type5 = (Class<?>) CoerceLuaToJava.coerce(args.arg(5), Class.class);
				return CoerceJavaToLua.coerce(registry.view(type0, type1, type2, type3, type4, type5));
			}
		});
		register("view7", new VarArgFunction() {
			@Override
			public LuaValue invoke(Varargs args) {
				Registry registry = m_engine.getRegistry();
				var type0 = (Class<?>) CoerceLuaToJava.coerce(args.arg(0), Class.class);
				var type1 = (Class<?>) CoerceLuaToJava.coerce(args.arg(1), Class.class);
				var type2 = (Class<?>) CoerceLuaToJava.coerce(args.arg(2), Class.class);
				var type3 = (Class<?>) CoerceLuaToJava.coerce(args.arg(3), Class.class);
				var type4 = (Class<?>) CoerceLuaToJava.coerce(args.arg(4), Class.class);
				var type5 = (Class<?>) CoerceLuaToJava.coerce(args.arg(5), Class.class);
				var type6 = (Class<?>) CoerceLuaToJava.coerce(args.arg(6), Class.class);
				return CoerceJavaToLua.coerce(registry.view(type0, type1, type2, type3, type4, type5, type6));
			}
		});
		register("view8", new VarArgFunction() {
			@Override
			public LuaValue invoke(Varargs args) {
				Registry registry = m_engine.getRegistry();
				var type0 = (Class<?>) CoerceLuaToJava.coerce(args.arg(0), Class.class);
				var type1 = (Class<?>) CoerceLuaToJava.coerce(args.arg(1), Class.class);
				var type2 = (Class<?>) CoerceLuaToJava.coerce(args.arg(2), Class.class);
				var type3 = (Class<?>) CoerceLuaToJava.coerce(args.arg(3), Class.class);
				var type4 = (Class<?>) CoerceLuaToJava.coerce(args.arg(4), Class.class);
				var type5 = (Class<?>) CoerceLuaToJava.coerce(args.arg(5), Class.class);
				var type6 = (Class<?>) CoerceLuaToJava.coerce(args.arg(6), Class.class);
				var type7 = (Class<?>) CoerceLuaToJava.coerce(args.arg(7), Class.class);
				return CoerceJavaToLua.coerce(registry.view(type0, type1, type2, type3, type4, type5, type6, type7).stream().toArray());
			}
		});
		register("create_entity", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				Registry registry = m_engine.getRegistry();
				Entity entity = registry.create();
				return CoerceJavaToLua.coerce(entity);
			}
		});
		register("emplace_entity", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				Registry registry = m_engine.getRegistry();
				Entity entity = registry.emplace(CoerceLuaToJava.coerce((LuaValue) args, Object[].class));
				return CoerceJavaToLua.coerce(entity);
			}
		});
		register("serialize_entity", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg0) {
				Registry registry = m_engine.getRegistry();
				Entity entity = (Entity) CoerceLuaToJava.coerce(arg0, Entity.class);
				String json = registry.serialize(entity);
				return CoerceJavaToLua.coerce(json);
			}
		});
		register("deserialize_entity", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg0) {
				Registry registry = m_engine.getRegistry();
				String json = (String) CoerceLuaToJava.coerce(arg0, String.class);
				Entity entity = registry.deserialize(json);
				return CoerceJavaToLua.coerce(entity);
			}
		});
		register("destroy_entity", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg0) {
				Registry registry = m_engine.getRegistry();
				Entity entity = (Entity) CoerceLuaToJava.coerce(arg0, Entity.class);
				registry.destroy(entity);
				return null;
			}
		});
	}

	@Override
	public LuaEngine getEngine() {
		return m_engine;
	}
}
