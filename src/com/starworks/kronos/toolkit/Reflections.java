package com.starworks.kronos.toolkit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Reflections {

	public static Object newInstance(String fqn) {
		try {
			Class<?> clazz = Class.forName(fqn);
			Constructor<?> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			return clazz.cast(constructor.newInstance());
		} catch (ClassNotFoundException e) {
			System.err.println("Class not found: " + fqn);
		} catch (NoSuchMethodException e) {
			System.err.println("Public parameterless constructor not found: " + fqn);
		} catch (IllegalAccessException e) {
			System.err.println("Illegal access: " + fqn);
			e.printStackTrace(System.err);
		} catch (InstantiationException e) {
			System.err.println("Instantiation exception: " + fqn);
			e.printStackTrace(System.err);
		} catch (InvocationTargetException e) {
			System.err.println("Invocation target exception: " + fqn);
			e.printStackTrace(System.err);
		}
		
		return null;
	}
	
	public static <T> List<Class<? extends T>> getSubclasses(String packageName, Class<T> superclass) {
	    List<Class<? extends T>> subclasses = new ArrayList<>();
	    try {
	        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	        String path = packageName.replace('.', '/');
	        Enumeration<URL> resources = classLoader.getResources(path);
	        List<File> dirs = new ArrayList<>();
	        while (resources.hasMoreElements()) {
	            URL resource = resources.nextElement();
	            dirs.add(new File(resource.getFile()));
	        }
	        for (File directory : dirs) {
	            subclasses.addAll(findClasses(directory, packageName, superclass));
	        }
	    } catch (IOException | ClassNotFoundException e) {
	        e.printStackTrace();
	    }
	    return subclasses;
	}
	
	private static <T> List<Class<? extends T>> findClasses(File directory, String packageName, Class<? extends T> superclass) throws ClassNotFoundException {
	    List<Class<? extends T>> classes = new ArrayList<>();
	    if (!directory.exists()) {
	        return classes;
	    }
	    File[] files = directory.listFiles();
	    for (File file : files) {
	        if (file.isDirectory()) {
	            assert !file.getName().contains(".");
	            classes.addAll(findClasses(file, packageName + "." + file.getName(), superclass));
	        } else if (file.getName().endsWith(".class")) {
	            @SuppressWarnings("unchecked")
				Class<? extends T> clazz = (Class<? extends T>) Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
	            if (superclass.isAssignableFrom(clazz) && !superclass.equals(clazz)) {
	                classes.add(clazz);
	            }
	        }
	    }
	    return classes;
	}
}
