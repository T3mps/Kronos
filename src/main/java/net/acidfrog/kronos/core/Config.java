package net.acidfrog.kronos.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.regex.Pattern;

import net.acidfrog.kronos.core.architecture.Kronos;
import net.acidfrog.kronos.core.lang.annotations.Internal;
import net.acidfrog.kronos.core.lang.error.KronosError;
import net.acidfrog.kronos.core.lang.logger.Logger;

public @Internal class Config extends Hashtable<String, Object> {
    private static final long serialVersionUID = 1L;

    private static Config instance;

    private static final String PATH = "data/.properties";

    private static final Pattern[] PATTERN = new Pattern[] { Pattern.compile("^(true|false)$"),    // boolean
                                                             Pattern.compile("^[0-9]+$"),          // integer
                                                             Pattern.compile("^[0-9]+\\.[0-9]+$"), // float
                                                             Pattern.compile("^\"[^\\s]+\"$"), };  // string

    private static final Map<Pattern, Function<String, Object>> UNMARSHALL = Map.of( PATTERN[0], Boolean::valueOf,
                                                                                     PATTERN[1], Integer::valueOf,
                                                                                     PATTERN[2], Float::valueOf,
                                                                                     PATTERN[3], String::valueOf );

    
    public static final String OPERATING_SYSTEM = OSArbiter.getName();

    public static final String OPERATING_SYSTEM_ARCHITECTURE = OSArbiter.getArchitecture();

    public static final String JAVA_VERSION = System.getProperty("java.version");
    
    public static final String JAVA_VENDOR = System.getProperty("java.vendor");
    
    public static final String OPENGL_VERSION = "OpenGL 4.6";

    private Config() {
        super(14, 0.75f);
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config().load();
        }
        return instance;
    }

    public Config load() throws KronosError {
        try (FileInputStream fis = new FileInputStream(PATH)) {
            Properties properties = new Properties();
            properties.load(fis);
            
            for (String key : properties.stringPropertyNames()) {
                var value = properties.getProperty(key);

                if (value == null) continue;
                
                boolean matched = false;
                for (var pattern : PATTERN) {
                    if (pattern.matcher(value).matches()) {
                        var out = UNMARSHALL.get(pattern).apply(value);

                        if (pattern == PATTERN[3]) {
                            String x = out.toString();
                            out = x.substring(1, x.length() - 1);
                        }

                        put(key.trim(), out);
                        matched = true;
                        break;
                    }
                }

                if (!matched) throw new IllegalArgumentException("Property " + key + " has an invalid value: " + value);
            }
        } catch (IOException e) {
            Logger.logFatal("Failed to load settings: " + e.getMessage());
            Kronos.FORCE_CLOSE();
        }
        
        return this;
    }

    public <T> void set(String key, T value) {
        put(key, value);
    }
    
    public boolean getBoolean(String key) {
        return (boolean) get(key);
    }

    public int getInt(String key) {
        return (int) get(key);
    }

    public float getFloat(String key) {
        return (float) get(key);
    }

    public String getString(String key) {
        return (String) get(key);
    }

    @Override
    public String toString() {
        int max = 0;
        for (var entry : entrySet()) if (entry.getKey().length() > max) {
            max = entry.getKey().length();
        }

        String fmt = "%-" + max + "s = %s\n";

        StringBuilder sb = new StringBuilder();

        for (var entry : entrySet()) sb.append(String.format(fmt, entry.getKey(), get(entry.getKey())));

        return sb.toString();
    }

    public final class OSArbiter {

        /** Name of 'win'. */
        public static final String WINDOWS         =   "Windows";

        /** Name of 'mac'. */
        public static final String MAC             =   "Darwin";

        /** Name of 'nux'. */
        public static final String LINUX           =   "Linux";

        /** Unsuppored OS. */
        public static final String NOT_SUPPORTED   =   "NOT_SUPPORTED";

        /** Cached OS. */
        private static String os = null;

        private static String arch = null;
        
        /**
         * Determines the current operating system, and caches it.
         * 
         * @return The current operating system.
         */
        public static String getName() {
            if (os == null) {
                String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            
                if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) os = MAC;
                else if (OS.indexOf("win") >= 0) os = WINDOWS;
                else if (OS.indexOf("nux") >= 0) os = LINUX;
                else os = NOT_SUPPORTED;
            }

            return os;
        }

        /**
         * Determines the current operating system architecture, either 32 or 64.
         * 
         * @return The current operating system architecture.
         */
        public static String getArchitecture() {
            if (arch == null) {
                String ac = System.getProperty("os.arch");
                
                if (ac.indexOf("64") >= 0) arch = "x64";
                else arch = "x32";
            }

            return arch;
        }

    }
    
}