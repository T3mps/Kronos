package net.acidfrog.kronos.renderer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

import net.acidfrog.kronos.core.lang.logger.Logger;

public class Shader {

    private int shaderProgramID;
    private String vertexShader, fragmentShader, geometryShader;
    private String path;

    private static final String HEADER = "#type";
    private static final String VERTEX_SHADER = "vertex";
    private static final String FRAGMENT_SHADER = "fragment";
    private static final String GEOMETRY_SHADER = "geometry";

    public Shader(String path) {
        try (InputStream in = Files.newInputStream(Paths.get(this.path = path))) {
            String source = new String(in.readAllBytes());
            String[] lines = source.split("(" + HEADER + ")( )+([a-zA-Z]+)");

            // check for presence of both vertex and fragment shader
            if (lines.length < 2) Logger.instance.logError("Shader '" + path + "' is not a valid shader");

            String[] shadertype = new String[lines.length - 1];
            int count = 1;
            int startPos = 0;
            int endPos = 0;
            
            while (count < lines.length) {
                startPos = source.indexOf(HEADER, endPos) + 6;
                endPos = source.indexOf("\r\n", startPos);
                shadertype[count-1] = source.substring(startPos, endPos).trim();

                switch (shadertype[count - 1]) {
                    case VERTEX_SHADER:
                        vertexShader = lines[count];
                        Logger.instance.logInfo("Vertex shader '" + path + "' loaded");
                        break;
                    case FRAGMENT_SHADER:
                        fragmentShader = lines[count];
                        Logger.instance.logInfo("Fragment shader '" + path + "' loaded");
                        break;
                    case GEOMETRY_SHADER:
                        geometryShader = lines[count];
                        Logger.instance.logWarn("Geometry shader '" + path + "' not yet supported");
                        break;
                    default: Logger.instance.logError("Shader '" + path + "' has invalid types");
                }
                count++;
            }
        } catch (IOException e) {
            Logger.instance.logError("Could not open shader: '" + path +"'");
        }
    }

    public Shader load() {
        shaderProgramID = GL30.glCreateProgram();

        // compile the shaders
        int vertexID = GL30.glCreateShader(GL30.GL_VERTEX_SHADER);
        int fragmentID = GL30.glCreateShader(GL30.GL_FRAGMENT_SHADER);

        GL30.glShaderSource(vertexID, vertexShader);
        GL30.glCompileShader(vertexID);

        int success = GL30.glGetShaderi(vertexID, GL30.GL_COMPILE_STATUS);
        if (success == GL30.GL_FALSE) {
            Logger.instance.logError("Vertex shader '" + path + "' failed to compile");
            Logger.instance.logError(GL30.glGetShaderInfoLog(vertexID, 1024));
        }

        GL30.glShaderSource(fragmentID, fragmentShader);
        GL30.glCompileShader(fragmentID);

        success = GL30.glGetShaderi(fragmentID, GL30.GL_COMPILE_STATUS);
        if (success == GL30.GL_FALSE) {
            Logger.instance.logError("Fragment shader '" + path + "' failed to compile");
            Logger.instance.logError(GL30.glGetShaderInfoLog(fragmentID, 1024));
        }
        
        // link the shader program
        GL30.glAttachShader(shaderProgramID, vertexID);
        GL30.glAttachShader(shaderProgramID, fragmentID);
        GL30.glLinkProgram(shaderProgramID);

        success = GL30.glGetProgrami(shaderProgramID, GL30.GL_LINK_STATUS);
        if (success == GL30.GL_FALSE) {
            Logger.instance.logError("Shader '" + path + "' failed to link");
            Logger.instance.logError(GL30.glGetProgramInfoLog(shaderProgramID, 1024));
        }
        return this;
    }

    public void bind() {
        GL30.glUseProgram(shaderProgramID);
    }

    public void unbind() {
        GL30.glUseProgram(0);
    }

    public Shader setUniform(String name, Matrix4f matrix) {
        int location = GL30.glGetUniformLocation(shaderProgramID, name);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        matrix.get(buffer);
        GL30.glUniformMatrix4fv(location, false, buffer);
        return this;
    }

    public void uploadUniform(String name, Matrix4f value) {
        
    }

    public int getShaderID() {
        return shaderProgramID;
    }

    public String getPath() {
        return path;
    }

}
