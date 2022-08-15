package net.acidfrog.kronos.toolkit;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

import net.acidfrog.kronos.scribe.Logger;
import net.acidfrog.kronos.scribe.LoggerFactory;

public final class Configuration implements Closeable {

    private static final int SUPPORTED_TYPES = new Class<?>[] { String.class, Integer.class, Double.class, Boolean.class }.length;

    private static final Pattern[] PATTERNS = new Pattern[] {
        Pattern.compile("^(true|false)$"), // boolean
        Pattern.compile("^(\\d+)$"),       // int
        Pattern.compile("-?\\d+\\.\\d+"),  // double
        Pattern.compile("^(.*)$"),         // string
    };

    private static final Map<Pattern, Function<String, Object>> UNMARSHALLERS = Map.of(
        PATTERNS[0], Boolean::parseBoolean,
        PATTERNS[1], Integer::parseInt,
        PATTERNS[2], Double::parseDouble,
        PATTERNS[3], String::valueOf
    );

    private final String filename;
    private final Set<String> keys;
    
    private final Map<String, Boolean> bools;
    private final Map<String, Integer> ints;
    private final Map<String, Double> doubles;
    private final Map<String, String> strings;

    private final Map<String, Class<?>> typeCache;

    private Logger logger;
    private boolean logMessages;

    private Configuration(String filename) {
        this.filename = filename;
        this.bools = new HashMap<String, Boolean>();
        this.ints = new HashMap<String, Integer>();
        this.doubles = new HashMap<String, Double>();
        this.strings = new HashMap<String, String>();
        this.keys = new HashSet<String>();
        this.typeCache = new HashMap<String, Class<?>>();
        this.logger = LoggerFactory.get("kvloader", Configuration.class);
        this.logMessages = true;
        
        parse();
    }

    public Configuration shouldLogMessages(boolean logMessages) {
        this.logMessages = logMessages;
        return this;
    }

    public static final Configuration load(String filename) {
        return new Configuration(filename);
    }

    public Object get(String key) {
        Class<?> type = typeCache.get(key);
        if (type == null) {
            throw new RuntimeException("Unknown key: " + key);
        }
        
        if (type == Boolean.class) {
            return bools.get(key);
        }
        if (type == Integer.class) {
            return ints.get(key);
        }
        if (type == Double.class) {
            return doubles.get(key);
        }
        if (type == String.class) {
            return strings.get(key);
        }

        throw new RuntimeException("Unknown type: " + type);
    }

    public boolean getBoolean(String key) {
        if (!bools.containsKey(key)) {
            if (!keys.contains(key)) {
                throw new RuntimeException("Unknown key: " + key);
            }
            throw new RuntimeException(key + " does not represent a boolean value");
        }
        return bools.get(key);
    }

    public int getInt(String key) {
        if (!ints.containsKey(key)) {
            if (!keys.contains(key)) {
                throw new RuntimeException("Unknown key: " + key);
            }
            throw new RuntimeException(key + " does not represent an integer value");
        }
        return ints.get(key);
    }

    public double getDouble(String key) {
        if (!doubles.containsKey(key)) {
            if (!keys.contains(key)) {
                throw new RuntimeException("Unknown key: " + key);
            }
            throw new RuntimeException(key + " does not represent a double value");
        }
        return doubles.get(key);
    }

    public String getString(String key) {
        if (!strings.containsKey(key)) {
            if (!keys.contains(key)) {
                throw new RuntimeException("Unknown key: " + key);
            }
            throw new RuntimeException(key + " does not represent a string value");
        }
        return strings.get(key);
    }

    public <V> Entry<V> put(String key, V value) {
        Class<?> type = value.getClass();
        
        if (type == Boolean.class) {
            bools.put(key, (Boolean) value);
            keys.add(key);
            typeCache.put(key, type);
            return new Entry<V>(key, value);
        }
        if (type == Integer.class) {
            ints.put(key, (Integer) value);
            keys.add(key);
            typeCache.put(key, type);
            return new Entry<V>(key, value);
        }
        if (type == Double.class || type == Float.class) {
            doubles.put(key, (Double) value);
            keys.add(key);
            typeCache.put(key, type);
            return new Entry<V>(key, value);
        }
        if (type == String.class) {
            strings.put(key, (String) value);
            keys.add(key);
            typeCache.put(key, type);
            return new Entry<V>(key, value);
        }
        
        throw new RuntimeException("Unknown type: " + type);
    }

    public boolean remove(String key) {
        boolean removed = keys.remove(key);
        if (removed) {
            typeCache.remove(key);
            bools.remove(key);
            ints.remove(key);
            doubles.remove(key);
            strings.remove(key);
        }

        return removed;
    }

    public boolean contains(String key) {
        return keys.contains(key);
    }

    public Configuration save(String filename) {
        Properties props = new Properties();

        for (var key : keys) {
            Object value = get(key);
            props.setProperty(key, ""+value);
        }

        try {
            props.store(new FileOutputStream(filename), null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    private void parse() {
        Properties props = new Properties();
        
        try {
            props.load(new FileInputStream(filename));

            for (var key : props.stringPropertyNames()) {
                String value = props.getProperty(key).replace("\"", "");
                unmarshall(key, value);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void unmarshall(String key, String value) {
        for (int i = 0; i < SUPPORTED_TYPES; i++) {
            var pattern = PATTERNS[i];

            if (pattern.matcher(value).matches()) {
                var unmarshalled = UNMARSHALLERS.get(pattern).apply(value);

                switch (i) {
                    case 0:
                        bools.put(key, (Boolean) unmarshalled);
                        keys.add(key);
                        typeCache.put(key, unmarshalled.getClass());

                        if (logMessages) {
                            logger.info("(boolean) '" + key + " = " + value + "' loaded.");
                        }
                        return;
                    case 1:
                        ints.put(key, (Integer) unmarshalled);
                        keys.add(key);
                        typeCache.put(key, unmarshalled.getClass());

                        if (logMessages) {
                            logger.info("    (int) '" + key + " = " + value + "' loaded.");
                        }
                        return;
                    case 2:
                        doubles.put(key, (Double) unmarshalled);
                        keys.add(key);
                        typeCache.put(key, unmarshalled.getClass());

                        if (logMessages) {
                            logger.info(" (double) '" + key + " = " + value + "' loaded.");
                        }
                        return;
                    case 3:
                        strings.put(key, (String) unmarshalled);
                        keys.add(key);
                        typeCache.put(key, unmarshalled.getClass());

                        if (logMessages) {
                            logger.info(" (String) '" + key + " = " + value + "' loaded.");
                        }
                        return;
                    default: throw new RuntimeException("Unknown type: " + unmarshalled.getClass());
                }
            }
        }
    }    
    
    public Set<String> keySet() {
        return keys;
    }

    @Override
    public void close() throws IOException {
        save(filename);
    }

    private record Entry<V>(String key, V value) {
    }
}
