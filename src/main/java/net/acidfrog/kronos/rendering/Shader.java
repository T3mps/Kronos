package net.acidfrog.kronos.rendering;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import net.acidfrog.kronos.core.lang.error.KronosError;
import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;
import net.acidfrog.kronos.math.Matrix2f;
import net.acidfrog.kronos.math.Matrix3f;
import net.acidfrog.kronos.math.Matrix4f;
import net.acidfrog.kronos.math.Vector2f;
import net.acidfrog.kronos.math.Vector3f;
import net.acidfrog.kronos.math.Vector4f;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public final class Shader {

    private static final String HEADER = "#type";
    private static final int HEADER_OFFSET = new String(HEADER + " ").length();

    private static final String VERTEX_SHADER = "vertex";
    private static final String FRAGMENT_SHADER = "fragment";
    private static final String GEOMETRY_SHADER = "geometry";

    private String path;
    private String vertexShaderSource;
    private String fragmentShaderSource;
    private int shaderProgram;
    private boolean current;

    public Shader(String path) {
        this.path = path;
        
        String source = null;
        try {
            source = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            throw new KronosError(KronosErrorLibrary.SHADER_FILE_NOT_FOUND);
        }

        String[] splitString = source.split("(" + HEADER + ")( )+([a-zA-Z]+)");
        if (splitString.length < 2) throw new KronosError(KronosErrorLibrary.VERTEX_OR_FRAGMENT_SHADER_NOT_FOUND);

        String[] shaderType = new String[splitString.length-1];
        int count = 1;
        int start = 0;
        int end = 0;

        while (count < splitString.length) {
            start = source.indexOf(HEADER, end) + HEADER_OFFSET;

            try { // windows support
                end = source.indexOf("\r\n", start);
            } catch (Exception e) { // linux / macOS support
                end = source.indexOf("\n", start);
            }

            shaderType[count - 1] = source.substring(start, end).trim();

            switch (shaderType[count - 1]) {
                case VERTEX_SHADER  : this.vertexShaderSource = splitString[count];   break;
                case FRAGMENT_SHADER: this.fragmentShaderSource = splitString[count]; break;
                case GEOMETRY_SHADER: throw new UnsupportedOperationException("Geometry shader not *yet* supported.");
                default: throw new KronosError(KronosErrorLibrary.UNSUPPORTED_SHADER_TYPE);
            }

            count++;
        }

        this.current = false;
    }

    public Shader compile() {
        int vertexID = glCreateShader(GL_VERTEX_SHADER);

        glShaderSource(vertexID, vertexShaderSource);
        glCompileShader(vertexID);

        if (glGetShaderi(vertexID, GL_COMPILE_STATUS) == GL_FALSE) throw new KronosError(KronosErrorLibrary.VERTEX_SHADER_COMPILATION_FAILED, glGetShaderInfoLog(vertexID, 1024));
        
        int fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(fragmentID, fragmentShaderSource);
        glCompileShader(fragmentID);

        if (glGetShaderi(fragmentID, GL_COMPILE_STATUS) == GL_FALSE) throw new KronosError(KronosErrorLibrary.FRAGMENT_SHADER_COMPILATION_FAILED, glGetShaderInfoLog(fragmentID, 1024));

        shaderProgram = glCreateProgram();

        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);

        if (glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_FALSE) throw new KronosError(KronosErrorLibrary.SHADER_LINKING_FAILED, glGetProgramInfoLog(shaderProgram, 1024));

        glDeleteShader(vertexID);
        glDeleteShader(fragmentID);

        return this;
    }

    public void bind() {
        if (!current) {
            glUseProgram(shaderProgram);
            current = true;
        }
    }

    public void unbind() {
        if (current) {
            glUseProgram(0);
            current = false;
        }
    }

    public void destroy() {
        glDeleteProgram(shaderProgram);
    }

    public void uploadBoolean(String name, boolean value) {
        bind();
        glUniform1i(glGetUniformLocation(shaderProgram, name), value ? 1 : 0);
    }
    
    public void uploadInt(String name, int value) {
        bind();
        glUniform1i(glGetUniformLocation(shaderProgram, name), value);
    }
    
    public void uploadIntArray(String name, int[] values) {
        bind();
        glUniform1iv(glGetUniformLocation(shaderProgram, name), values);
    }

    public void uploadFloat(String name, float value) {
        bind();
        glUniform1f(glGetUniformLocation(shaderProgram, name), value);
    }

    public void uploadFloatArray(String name, float[] values) {
        bind();
        glUniform1fv(glGetUniformLocation(shaderProgram, name), values);
    }

    public void uploadVector2(String name, Vector2f vector) {
        bind();
        glUniform2f(glGetUniformLocation(shaderProgram, name), vector.x, vector.y);
    }

    public void uploadVector3(String name, Vector3f vector) {
        bind();
        glUniform3f(glGetUniformLocation(shaderProgram, name), vector.x, vector.y, vector.z);
    }

    public void uploadVector4(String name, Vector4f vector) {
        bind();
        glUniform4f(glGetUniformLocation(shaderProgram, name), vector.x, vector.y, vector.z, vector.w);
    }

    public void uploadMatrix2(String name, Matrix2f matrix) {
        bind();
        glUniformMatrix2fv(glGetUniformLocation(shaderProgram, name), false, matrix.get(new float[4]));
    }

    public void uploadMatrix3(String name, Matrix3f matrix) {
        bind();
        glUniformMatrix3fv(glGetUniformLocation(shaderProgram, name), false, matrix.get(new float[9]));
    }

    public void uploadMatrix4(String name, Matrix4f matrix) {
        bind();
        glUniformMatrix4fv(glGetUniformLocation(shaderProgram, name), false, matrix.get(new float[16]));
    }

    public void uploadTexture(String name, int slot) {
        bind();
        glUniform1i(glGetUniformLocation(shaderProgram, name), slot);
    }

    public String getPath() {
        return path;
    }

    public boolean isCurrent() {
        return current;
    }
    
}
