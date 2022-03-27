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

	/** Returns the Class<?> object associated with the class or interface with the supplied string name. */
	public static Class<?> forName(String name) throws ReflectionException {
		try {
			return Class.forName(name);
		} catch(ClassNotFoundException e) {
			throw new ReflectionException(e, "Class<?> not found: " + name);
		}
	}

	/** Returns the simple name of the underlying class as supplied in the source code. */
	public static String getSimpleName(Class<?> clazz) {
		return clazz.getSimpleName();
	}

	/** Determines if the supplied Object is assignment-compatible with the object represented by supplied Class. */
	public static boolean isInstance(Object obj, Class<?> clazz) {
		return clazz.isInstance(obj);
	}

	/** Determines if the class or interface represented by first Class<?> parameter is either the same as, or is a superclass or
	 * superinterface of, the class or interface represented by the second Class<?> parameter. */
	public static boolean isAssignableFrom(Class<?> c1, Class<?> c2) {
		return c1.isAssignableFrom(c2);
	}

	/** Returns true if the class or interface represented by the supplied Class<?> is a member class. */
	public static boolean isMemberClass(Class<?> clazz) {
		return clazz.isMemberClass();
	}

	/** Returns true if the class or interface represented by the supplied Class<?> is a static class. */
	public static boolean isStaticClass(Class<?> clazz) {
		return Modifier.isStatic(clazz.getModifiers());
	}

	/** Determines if the supplied Class<?> object represents an array class. */
	public static boolean isArray(Class<?> clazz) {
		return clazz.isArray();
	}

	/** Determines if the supplied Class<?> object represents a primitive type. */
	public static boolean isPrimitive(Class<?> clazz) {
		return clazz.isPrimitive();
	}

	/** Determines if the supplied Class<?> object represents an enum type. */
	public static boolean isEnum(Class<?> clazz) {
		return clazz.isEnum();
	}

	/** Determines if the supplied Class<?> object represents an annotation type. */
	public static boolean isAnnotation(Class<?> clazz) {
		return clazz.isAnnotation();
	}

	/** Determines if the supplied Class<?> object represents an interface type. */
	public static boolean isInterface(Class<?> clazz) {
		return clazz.isInterface();
	}

	/** Determines if the supplied Class<?> object represents an abstract type. */
	public static boolean isAbstract(Class<?> clazz) {
		return Modifier.isAbstract(clazz.getModifiers());
	}

	/** Creates a new instance of the class represented by the supplied Class. */
	public static <T> T newInstance(Class<T> clazz) throws ReflectionException {
        try {
            Constructor<T> constructor = clazz.getConstructor();
            // Constructor<T>[] constructors = (Constructor<T>[]) getConstructors(clazz);
            constructor.setAccessible(true);

            return constructor.newInstance();
        } catch (NoSuchMethodException  | SecurityException        | InstantiationException |
                 IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Logger.instance.logFatal("Failed to create instance of class: " + clazz.getName());
            throw new ReflectionException(e, "Failed to create new instance of class: " + clazz.getName());
        }
	}

	/** Returns the Class<?> representing the component type of an array. If this class does not represent an array class this method
	 * returns null. */
	public static Class<?> getComponentType(Class<?> clazz) {
		return clazz.getComponentType();
	}

	/** Returns an array of {@link Constructor<T>} containing the public constructors of the class represented by the supplied
	 * Class. */
    @SuppressWarnings("unchecked")
	public static <T> Constructor<T>[] getConstructors(Class<T> clazz) {
		Constructor<?>[] constructors = clazz.getConstructors();
		Constructor<T>[] result = (Constructor<T>[]) new Constructor<?>[constructors.length];
		
        for (int i = 0, j = constructors.length; i < j; i++) result[i] =(Constructor<T>) constructors[i];

		return result;
	}

	/** 
	 * Returns a {@link Constructor<?>} that represents the public constructor for the supplied class which takes the supplied
	 * parameter types. 
	 * 
	 * @throws ReflectionException
	 */
	public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) throws ReflectionException {
		try {
            return clazz.getConstructor(parameterTypes);
        } catch(NoSuchMethodException e) {
            throw new ReflectionException(e, "No such constructor: " + clazz.getName() + "(" + Std.Strings.join(parameterTypes, ", ") + ")");
        }
	}

	/**
	 * Returns a {@link Constructor<?>} that represents the constructor for the supplied class which takes the supplied parameter
	 * types. 
	 * 
	 * @throws ReflectionException
	 */
	public static Constructor<?> getDeclaredConstructor(Class<?> clazz, Class<?>... parameterTypes) throws ReflectionException {
        try {
            return clazz.getDeclaredConstructor(parameterTypes);
        } catch(NoSuchMethodException e) {
            throw new ReflectionException(e, "No such constructor: " + clazz.getName() + "(" + Std.Strings.join(parameterTypes, ", ") + ")");
        }
	}

	/** Returns the elements of this enum class or null if this Class<?> object does not represent an enum type. */
	public static Object[] getEnumConstants(Class<?> clazz) {
		return clazz.getEnumConstants();
	}

	/** Returns an array of {@link Method} containing the public member methods of the class represented by the supplied Class. */
	public static Method[] getMethods(Class<?> clazz) {
        return clazz.getMethods();
	}

	/**
	 * Returns a {@link Method} that represents the public member method for the supplied class which takes the supplied parameter
	 * types. 
	 * 
	 * @throws ReflectionException 
	 */
	public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws ReflectionException {
		try {
            return clazz.getMethod(name, parameterTypes);
        } catch(NoSuchMethodException e) {
            throw new ReflectionException(e, "No such method: " + clazz.getName() + "." + name + "(" + Std.Strings.join(parameterTypes, ", ") + ")");
        }
	}

	/** Returns an array of {@link Method} containing the methods declared by the class represented by the supplied Class. */
	public static Method[] getDeclaredMethods(Class<?> clazz) {
        return clazz.getDeclaredMethods();
	}

	/** 
	 * Returns a {@link Method} that represents the method declared by the supplied class which takes the supplied parameter
	 * types.
	 * 
	 * @throws ReflectionException
	 */
	public static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws ReflectionException {
        try {
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch(NoSuchMethodException e) {
            throw new ReflectionException(e, "No such method: " + clazz.getName() + "." + name + "(" + Std.Strings.join(parameterTypes, ", ") + ")");
        }
	}

	/** Returns an array of {@link Field} containing the public fields of the class represented by the supplied Class. */
	public static Field[] getFields(Class<?> clazz) {
        return clazz.getFields();
	}

	/**
	 * Returns a {@link Field} that represents the specified public member field for the supplied class. 
	 *
	 * @throws ReflectionException
	 */
	public static Field getField(Class<?> clazz, String name) throws ReflectionException {
        try {
            return clazz.getField(name);
        } catch(NoSuchFieldException e) {
            throw new ReflectionException(e, "No such field: " + clazz.getName() + "." + name);
        }
	}

	/** Returns an array of {@link Field} objects reflecting all the fields declared by the supplied class. */
	public static Field[] getDeclaredFields(Class<?> clazz) {
        return clazz.getDeclaredFields();
	}

	/**
	 * Returns a {@link Field} that represents the specified declared field for the supplied class.
	 * 
	 * @throws ReflectionException
	 */
	public static Field getDeclaredField(Class<?> clazz, String name) throws ReflectionException {
        try {
            return clazz.getDeclaredField(name);
        } catch(NoSuchFieldException e) {
            throw new ReflectionException(e, "No such field: " + clazz.getName() + "." + name);
        }
	}

	/** Returns true if the supplied class includes an annotation of the given type. */
	public static boolean isAnnotationPresent(Class<?> clazz, Class<? extends Annotation> annotationType) {
		return clazz.isAnnotationPresent(annotationType);
	}

	/** Returns an array of {@link Annotation} objects reflecting all annotations declared by the supplied class, and inherited
	 * from its superclass. Returns an empty array if there are none. */
	public static Annotation[] getAnnotations(Class<?> clazz) {
        return clazz.getAnnotations();
	}

	/** Returns an {@link Annotation} object reflecting the annotation provided, or null if this class doesn't have such an
	 * annotation. This is a convenience function if the caller knows already which annotation type he's looking for. */
	public static Annotation getAnnotation(Class<?> clazz, Class<? extends Annotation> annotationType) {
        return clazz.getAnnotation(annotationType);
	}

	/** Returns an array of {@link Annotation} objects reflecting all annotations declared by the supplied class, or an empty array
	 * if there are none. Does not include inherited annotations. */
	public static Annotation[] getDeclaredAnnotations(Class<?> clazz) {
        return clazz.getDeclaredAnnotations();
	}

	/** Returns an {@link Annotation} object reflecting the annotation provided, or null if this class doesn't have such an
	 * annotation. This is a convenience function if the caller knows already which annotation type he's looking for. */
	public static Annotation getDeclaredAnnotation(Class<?> clazz, Class<? extends java.lang.annotation.Annotation> annotationType) {
        return clazz.getDeclaredAnnotation(annotationType);
	}

	public static Class<?>[] getInterfaces(Class<?> clazz) {
		return clazz.getInterfaces();
	}

	static public Object newInstance (Class<?> c, int size) {
		return Array.newInstance(c, size);
	}

	/** Returns the length of the supplied array. */
	static public int getLength (Object array) {
		return Array.getLength(array);
	}

	/** Returns the value of the indexed component in the supplied array. */
	static public Object get (Object array, int index) {
		return Array.get(array, index);
	}

	/** Sets the value of the indexed component in the supplied array to the supplied value. */
	static public void set (Object array, int index, Object value) {
		Array.set(array, index, value);
	}

}