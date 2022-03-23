package net.acidfrog.kronos.core.io.serialization;

import java.lang.reflect.Field;

public class Kron {
    
    public static final String EXTENSION = "kron";
    public static final String DOT_EXTENSION = "." + EXTENSION;
    public static final int SERIALIZABLE_MODIFIER = 128; // transient modifier
    public static final byte[] HEADER = EXTENSION.getBytes();
    public static final short VERSION = 0x0100;
    
    private final KronDatabase database;

    public Kron(String name) {
        this.database = new KronDatabase(name);
    }

    public void serializeToFile(String path, Object... objects) throws KronException, IllegalArgumentException, IllegalAccessException {
        if (!path.contains(DOT_EXTENSION)) path += DOT_EXTENSION;
        
        KronObject obj = null;
        for (Object object : objects) {
            if (!object.getClass().isAnnotationPresent(Serializable.class)) throw new IllegalArgumentException("Object is not serializable: " + object.getClass().getName());

            obj = new KronObject(database.getName() + "." + object.getClass().getSimpleName());
            Field[] fields = object.getClass().getDeclaredFields();

            for (Field f : fields) {
                Class<?> clazz = f.getType();
    
                if (f.getModifiers() == SERIALIZABLE_MODIFIER) {
                    if (clazz.equals(Boolean.TYPE)   || clazz.equals(Byte.TYPE)   ||
                        clazz.equals(Character.TYPE) || clazz.equals(Short.TYPE)  ||
                        clazz.equals(Integer.TYPE)   || clazz.equals(Long.TYPE)   ||
                        clazz.equals(Float.TYPE)     || clazz.equals(Double.TYPE)) {
    
                        f.setAccessible(true);
                        if (clazz.isArray()) {
                            KronArray ka = KronArray.create(f.getName(), (Object[]) f.get(object));
                            obj.addArray(ka);
                        } else {
                            KronField kf = KronField.create(f.getName(), f.get(object));
                            obj.addField(kf);
                        }
                    } else if (clazz.equals(String.class)) {
                        f.setAccessible(true);
                        KronString ks = KronString.create(f.getName(), (String) f.get(object));
                        obj.addString(ks);
                    }
                }
            }

            if (!obj.isEmpty() && obj != null) {
                database.addObject(obj);
                database.serializeToFile(path);
            } else throw new KronException("Object is empty or contains no serializable fields.");
        }
    }

}
