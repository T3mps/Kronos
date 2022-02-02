package net.acidfrog.kronos.core;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.acidfrog.kronos.core.lang.Std;
import net.acidfrog.kronos.core.lang.logger.Logger;

public class Settings {

    public static final String PATH = "data/.config";
    private static final Path directory = Paths.get(PATH);

    private static final String HEADER = "{";
    private static final String FOOTER = "}";
    private static final String DELIMITER = ",";
    private static final String SEPARATOR = ".";
    private static final String DEFINE = "\"";
    private static final String COMMENT = "#";

    // RUNTIME
    public static final boolean DEBUG              =   readProperty("kronos.debug", false) | readProperty("joml.debug", false);
    public static final boolean RELEASE            =   readProperty("kronos.release", false);

    public static final int REGULAR_UPDATE_RATE    =   readProperty("kronos.regularUpdateRate", 60);
    public static final int PHYSICS_UPDATE_RATE    =   readProperty("kronos.physicsUpdateRate", 50);
    
    public static final boolean FASTMATH           =   readProperty("joml.fastmath", false);
    public static final boolean IMPRECISE_MATH     =   readProperty("joml.impreciseMath", false);
    public static final boolean SIN_LOOKUP_TABLE   =   readProperty("joml.sinLookup", false);

    static final boolean readProperty(String name, boolean defaultValue) {
        try(BufferedReader reader = Files.newBufferedReader(directory)) {
            while(reader.readLine() != null) {
                String line = reader.readLine().trim();
                if(Std.Strings.startsWith(line, COMMENT)) continue;
                System.out.println("[Settings] " + line);
                return Boolean.parseBoolean(line);
            }
        } catch (Exception e) {
            Logger.instance.logError("Could not read property " + name + " from " + directory.toString() + ": " + e.getMessage());
            return defaultValue;
        }
        return defaultValue;
    }

    static final int readProperty(String name, int defaultValue) {
        return defaultValue;
    }
    
}
