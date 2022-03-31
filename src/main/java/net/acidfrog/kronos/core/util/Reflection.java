package net.acidfrog.kronos.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.management.ReflectionException;

import net.acidfrog.kronos.core.lang.Std;
import net.acidfrog.kronos.core.lang.logger.Logger;

public final class Reflection {

	private Reflection() {}

	public static Class<?> forName(String name) throws ReflectionException {
		try {
			return Class.forName(name);
		} catch(ClassNotFoundException e) {
			throw new ReflectionException(e, "Class<?> not found: " + name);
		}
	}

	public static String getSimpleName(Class<?> clazz) {
		return clazz.getSimpleName();
	}

	public static boolean isInstance(Object obj, Class<?> clazz) {
		return clazz.isInstance(obj);
	}

	public static boolean isAssignableFrom(Class<?> c1, Class<?> c2) {
		return c1.isAssignableFrom(c2);
	}

	public static boolean isMemberClass(Class<?> clazz) {
		return clazz.isMemberClass();
	}

	public static boolean isStaticClass(Class<?> clazz) {
		return Modifier.isStatic(clazz.getModifiers());
	}

	public static boolean isArray(Class<?> clazz) {
		return clazz.isArray();
	}

	public static boolean isPrimitive(Class<?> clazz) {
		return clazz.isPrimitive();
	}

	public static boolean isEnum(Class<?> clazz) {
		return clazz.isEnum();
	}

	public static boolean isAnnotation(Class<?> clazz) {
		return clazz.isAnnotation();
	}

	public static boolean isInterface(Class<?> clazz) {
		return clazz.isInterface();
	}

	public static boolean isAbstract(Class<?> clazz) {
		return Modifier.isAbstract(clazz.getModifiers());
	}

	public static <T> T instantiate(Class<T> clazz) throws ReflectionException {
        try {
            Constructor<T> constructor = clazz.getConstructor();
            // Constructor<T>[] constructors = (Constructor<T>[]) getConstructors(clazz);
            constructor.setAccessible(true);

            return constructor.newInstance();
        } catch (NoSuchMethodException  | SecurityException        | InstantiationException    |
                 IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Logger.instance.logFatal("Failed to create instance of class: " + clazz.getName());
            throw new ReflectionException(e, "Failed to create new instance of class: " + clazz.getName());
        }
	}


	public static Class<?> getComponentType(Class<?> clazz) {
		return clazz.getComponentType();
	}

    @SuppressWarnings("unchecked")
	public static <T> Constructor<T>[] getConstructors(Class<T> clazz) {
		Constructor<?>[] constructors = clazz.getConstructors();
		Constructor<T>[] result = (Constructor<T>[]) new Constructor<?>[constructors.length];
		
        for (int i = 0, j = constructors.length; i < j; i++) result[i] =(Constructor<T>) constructors[i];

		return result;
	}

	public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) throws ReflectionException {
		try {
            return clazz.getConstructor(parameterTypes);
        } catch(NoSuchMethodException e) {
            throw new ReflectionException(e, "No such constructor: " + clazz.getName() + "(" + Std.Strings.join(parameterTypes, ", ") + ")");
        }
	}

	public static Constructor<?> getDeclaredConstructor(Class<?> clazz, Class<?>... parameterTypes) throws ReflectionException {
        try {
            return clazz.getDeclaredConstructor(parameterTypes);
        } catch(NoSuchMethodException e) {
            throw new ReflectionException(e, "No such constructor: " + clazz.getName() + "(" + Std.Strings.join(parameterTypes, ", ") + ")");
        }
	}

	public static Object[] getEnumConstants(Class<?> clazz) {
		return clazz.getEnumConstants();
	}

	public static Method[] getMethods(Class<?> clazz) {
        return clazz.getMethods();
	}

	public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws ReflectionException {
		try {
            return clazz.getMethod(name, parameterTypes);
        } catch(NoSuchMethodException e) {
            throw new ReflectionException(e, "No such method: " + clazz.getName() + "." + name + "(" + Std.Strings.join(parameterTypes, ", ") + ")");
        }
	}

	public static Method[] getDeclaredMethods(Class<?> clazz) {
        return clazz.getDeclaredMethods();
	}

	public static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws ReflectionException {
        try {
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch(NoSuchMethodException e) {
            throw new ReflectionException(e, "No such method: " + clazz.getName() + "." + name + "(" + Std.Strings.join(parameterTypes, ", ") + ")");
        }
	}

	public static Field[] getFields(Class<?> clazz) {
        return clazz.getFields();
	}

	public static Field getField(Class<?> clazz, String name) throws ReflectionException {
        try {
            return clazz.getField(name);
        } catch(NoSuchFieldException e) {
            throw new ReflectionException(e, "No such field: " + clazz.getName() + "." + name);
        }
	}

	public static Field[] getDeclaredFields(Class<?> clazz) {
        return clazz.getDeclaredFields();
	}

	public static Field getDeclaredField(Class<?> clazz, String name) throws ReflectionException {
        try {
            return clazz.getDeclaredField(name);
        } catch(NoSuchFieldException e) {
            throw new ReflectionException(e, "No such field: " + clazz.getName() + "." + name);
        }
	}

	public static boolean isAnnotationPresent(Class<?> clazz, Class<? extends Annotation> annotationType) {
		return clazz.isAnnotationPresent(annotationType);
	}

	public static Annotation[] getAnnotations(Class<?> clazz) {
        return clazz.getAnnotations();
	}

	public static Annotation getAnnotation(Class<?> clazz, Class<? extends Annotation> annotationType) {
        return clazz.getAnnotation(annotationType);
	}

	public static Annotation[] getDeclaredAnnotations(Class<?> clazz) {
        return clazz.getDeclaredAnnotations();
	}
	
	public static Annotation getDeclaredAnnotation(Class<?> clazz, Class<? extends java.lang.annotation.Annotation> annotationType) {
        return clazz.getDeclaredAnnotation(annotationType);
	}

	public static Class<?>[] getInterfaces(Class<?> clazz) {
		return clazz.getInterfaces();
	}

	static public Object newInstance(Class<?> c, int size) {
		return Array.newInstance(c, size);
	}

	static public int getLength(Object array) {
		return Array.getLength(array);
	}

	static public Object get(Object array, int index) {
		return Array.get(array, index);
	}

	static public void set(Object array, int index, Object value) {
		Array.set(array, index, value);
	}

}