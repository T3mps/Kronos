package net.acidfrog.kronos.core.assets;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.acidfrog.kronos.core.lang.annotations.Internal;
import net.acidfrog.kronos.core.lang.logger.Logger;
import net.acidfrog.kronos.rendering.Shader;
import net.acidfrog.kronos.rendering.Spritesheet;
import net.acidfrog.kronos.rendering.Texture;

public class AssetManager {

    private static Map<String, File> files = new HashMap<String, File>();

    private static Map<String, Shader> shaders = new HashMap<String, Shader>();
    
    private static Map<String, Texture> textures = new HashMap<String, Texture>();

    private static Map<String, Spritesheet> spritesheets = new HashMap<String, Spritesheet>();

    @SuppressWarnings("unchecked")
    public static <T> T get(final String key, final Class<T> type) {
        switch(type.getSimpleName()) {
            case      "Shader": return (T) getShader(key);
            case     "Texture": return (T) getTexture(key);
            case "Spritesheet": return (T) getSpritesheet(key);
            default:
                Logger.logError("Unknown type: " + type.getSimpleName());
                return null;
        }
    }

    public static File getFile(String path) {
        if (files.containsKey(path)) return files.get(path);
        
        Logger.logInfo("Loading file: '" + path + "'");

        File file = new File(path);
        if (!file.exists()) {
            Logger.logError("File not found: '" + path + "'");
            return null;
        }
        
        files.put(path, file);
        
        return file;
    }

    public static Shader getShader(String path) {
        if (shaders.containsKey(path)) return shaders.get(path);

        Logger.logInfo("Loading shader: '" + path + "'");

        Shader shader = new Shader(path).compile();
        shaders.put(path, shader);

        return shader;
    }

    public static Texture getTexture(String path) {
        if (textures.containsKey(path)) return textures.get(path);

        Logger.logInfo("Loading texture: '" + path + "'");
        
        Texture texture = new Texture().initialize(path);
        textures.put(path, texture);

        return texture;
    }

    // meant to only be called in {@link Spritesheet#create(String, int, int, int, int)}
    public static @Internal void addSpritesheet(String path, Spritesheet spritesheet) {
        if (spritesheets.containsKey(path)) return;

        if (spritesheet == null) {
            Logger.logError("Spritesheet not found: '" + path + "'");
            return;
        }

        Logger.logInfo("Loading spritesheet: '" + path + "'");

        spritesheets.put(path, spritesheet);
    }

    public static Spritesheet getSpritesheet(String path) {
        if (spritesheets.containsKey(path)) return spritesheets.get(path);
        
        Logger.logError("Spritesheet not found: '" + path + "'");
        return null;
    }
    
}
