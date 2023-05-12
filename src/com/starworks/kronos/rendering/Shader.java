package com.starworks.kronos.rendering;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import com.starworks.kronos.files.FileHandle;
import com.starworks.kronos.files.FileSystem;
import com.starworks.kronos.logging.Logger;
import com.starworks.kronos.maths.Matrix2f;
import com.starworks.kronos.maths.Matrix3f;
import com.starworks.kronos.maths.Matrix4f;
import com.starworks.kronos.maths.Vector2f;
import com.starworks.kronos.maths.Vector3f;
import com.starworks.kronos.maths.Vector4f;

public final class Shader implements Closeable {
	private final Logger LOGGER = Logger.getLogger(Shader.class);

	private static enum Type {
		VERTEX, FRAGMENT, GEOMETRY;
	}

	private static enum State {
		UNINITIALIZED, COMPILED, LINKED, BOUND, UNBOUND, DISPOSED;

		public boolean isCanonical() {
			int ordinal = ordinal();
			return ordinal >= 2 && ordinal <= 4;
		}
	}

	private static final String VERTEX_SHADER_KEY = "vertex";
	private static final String FRAGMENT_SHADER_KEY = "fragment";
	private static final String GEOMETRY_SHADER_KEY = "geometry";

	private int m_vertexID;
	private int m_fragmentID;
	private int m_geometryID;
	private int m_programID;

	private final FileHandle m_fileHandle;
	private final String m_vertexSrc;
	private final String m_fragmentSrc;
	private final String m_geometrySrc;

	private State m_state;

	private final Map<String, Integer> m_uniformLocations;

	public Shader(String filepath) {
		this.m_vertexID = -1;
		this.m_fragmentID = -1;
		this.m_programID = -1;
		FileHandle file = null;
		try {
			file = FileSystem.getFileHandle(filepath, false, false);
		} catch (IOException e) {
			LOGGER.error("Error opening shader file", e);
		}
		this.m_fileHandle = file;

		String src;
		try {
			src = readShaderSource();
		} catch (IOException e) {
			throw new RuntimeException("Error: Could not open file for shader: '" + filepath + "'", e);
		}

		Map<Type, String> shaderSources = preprocess(src);
		m_vertexSrc = shaderSources.get(Type.VERTEX);
		m_fragmentSrc = shaderSources.get(Type.FRAGMENT);
		m_geometrySrc = shaderSources.get(Type.GEOMETRY);

		if (m_vertexSrc == null || m_fragmentSrc == null) {
			throw new RuntimeException("Error: Shader '" + filepath + "' must contain both vertex and fragment shaders");
		}
		this.m_state = State.UNINITIALIZED;
		this.m_uniformLocations = new HashMap<String, Integer>();
	}

	private String readShaderSource() throws IOException {
		StringBuilder source = new StringBuilder();

		try (InputStream is = this.m_fileHandle.readStream(); InputStreamReader isr = new InputStreamReader(is); BufferedReader reader = new BufferedReader(isr)) {

			String line;
			while ((line = reader.readLine()) != null) {
				source.append(line);
				source.append(System.lineSeparator());
			}
		}

		return source.toString();
	}

	private Map<Type, String> preprocess(String shaderSource) {
		Map<Type, String> shaderSources = new HashMap<Type, String>();
		String[] splitString = shaderSource.split("(#type)( )+([a-zA-Z]+)");

		if (splitString.length < 2) {
			System.out.println(shaderSource);
			throw new RuntimeException("Error: Shader '" + m_fileHandle.getFileName() + "' is not a valid shader");
		}

		final int size = splitString.length;
		int count = 1;
		int startPos = 0;
		int endPos = 0;

		while (count < size) {
			startPos = shaderSource.indexOf("#type", endPos) + 6;
			endPos = shaderSource.indexOf(System.lineSeparator(), startPos);
			String shaderType = shaderSource.substring(startPos, endPos).trim();

			shaderSources.put(switch (shaderType) {
			case VERTEX_SHADER_KEY -> Type.VERTEX;
			case FRAGMENT_SHADER_KEY -> Type.FRAGMENT;
			case GEOMETRY_SHADER_KEY -> Type.GEOMETRY;
			default -> throw new RuntimeException("Error: Shader '" + m_fileHandle.getFileName() + "' has invalid type: " + shaderType);
			}, splitString[count++]);
		}

		return shaderSources;
	}

	public Shader compile() {
		m_vertexID = -1;
		if (m_vertexSrc != null) {
			m_vertexID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
			GL20.glShaderSource(m_vertexID, m_vertexSrc);
			GL20.glCompileShader(m_vertexID);

			if (GL20.glGetShaderi(m_vertexID, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
				LOGGER.error("{0} vertex shader compilation failed!", m_fileHandle.toString());
				LOGGER.error(GL20.glGetShaderInfoLog(m_vertexID, GL20.glGetShaderi(m_vertexID, GL20.GL_INFO_LOG_LENGTH)));
			}
		}

		m_fragmentID = -1;
		if (m_fragmentSrc != null) {
			m_fragmentID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
			GL20.glShaderSource(m_fragmentID, m_fragmentSrc);
			GL20.glCompileShader(m_fragmentID);

			if (GL20.glGetShaderi(m_fragmentID, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
				LOGGER.error("{0} fragment shader compilation failed!", m_fileHandle.toString());
				LOGGER.error(GL20.glGetShaderInfoLog(m_fragmentID, GL20.glGetShaderi(m_fragmentID, GL20.GL_INFO_LOG_LENGTH)));
			}
		}

		m_geometryID = -1;
		if (m_geometrySrc != null) {
			m_geometryID = GL20.glCreateShader(GL32.GL_GEOMETRY_SHADER);
			GL20.glShaderSource(m_geometryID, m_geometrySrc);
			GL20.glCompileShader(m_geometryID);

			if (GL20.glGetShaderi(m_geometryID, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
				LOGGER.error("'{0}' geometry shader compilation failed!", m_fileHandle.toString());
				LOGGER.error(GL20.glGetShaderInfoLog(m_geometryID, GL20.glGetShaderi(m_geometryID, GL20.GL_INFO_LOG_LENGTH)));
			}
		}

		m_state = State.COMPILED;
		return this;
	}

	public Shader link() {
		m_programID = GL20.glCreateProgram();
		if (m_vertexID != -1) {
			GL20.glAttachShader(m_programID, m_vertexID);
		}
		if (m_fragmentID != -1) {
			GL20.glAttachShader(m_programID, m_fragmentID);
		}
		if (m_geometryID != -1) {
			GL20.glAttachShader(m_programID, m_geometryID);
		}

		GL20.glLinkProgram(m_programID);
		if (GL20.glGetProgrami(m_programID, GL20.GL_LINK_STATUS) == GL20.GL_FALSE) {
			LOGGER.error("{0} linking of shaders failed!", m_fileHandle.toString());
			LOGGER.error(GL20.glGetProgramInfoLog(m_programID, GL20.glGetProgrami(m_programID, GL20.GL_INFO_LOG_LENGTH)));
		}

		if (m_vertexID != -1) {
			GL20.glDetachShader(m_programID, m_vertexID);
			GL20.glDeleteShader(m_vertexID);
		}
		if (m_fragmentID != -1) {
			GL20.glDetachShader(m_programID, m_fragmentID);
			GL20.glDeleteShader(m_fragmentID);
		}
		if (m_geometryID != -1) {
			GL20.glDetachShader(m_programID, m_geometryID);
			GL20.glDeleteShader(m_geometryID);
		}
		m_state = State.LINKED;
		return this;
	}

	public void compileAndLink() {
		compile();
		link();
	}

	public void bind() {
		if (m_state.isCanonical() && (m_state == State.LINKED || m_state == State.UNBOUND)) {
			GL30.glUseProgram(m_programID);
			m_state = State.BOUND;
			return;
		}

		LOGGER.warn("Attempted to bind non-canonical or bound shader");
	}
	
	public void unbind() {
		if (m_state.isCanonical() && m_state == State.BOUND) {
			GL30.glUseProgram(0);
			m_state = State.UNBOUND;
			return;
		}

		LOGGER.warn("Attempted to unbind non-canonical or unbound shader");
	}

	public void unlink() {
		if (m_programID != -1 && m_state.isCanonical()) {
			GL20.glDeleteProgram(m_programID);
			m_state = State.DISPOSED;
			return;
		}

		LOGGER.warn("Attempted to dispose non-canonical or malformed");
	}

	@Override
	public void close() throws IOException {
		unlink();
	}
	
	public void setUniform(String name, boolean value) {
		if (m_state == State.BOUND) {
			int location = getUniformLocation(name);
			GL30.glUniform1i(location, value ? 1 : 0);
			return;
		}
		LOGGER.warn("Attempted to setUniform(boolean) {0} {1} on an unbound shader {2}", name, value, m_fileHandle.getFileName());
	}

	public void setUniform(String name, int value) {
		if (m_state == State.BOUND) {
			int location = getUniformLocation(name);
			GL30.glUniform1i(location, value);
			return;
		}
		LOGGER.warn("Attempted to setUniform(int) {0} {1} on an unbound shader {2}", name, value, m_fileHandle.getFileName());
	}

	public void setUniform(String name, int[] values) {
		if (m_state == State.BOUND) {
			int location = getUniformLocation(name);
			GL30.glUniform1iv(location, values);
			return;
		}
		LOGGER.warn("Attempted to setUniform(int[]) {0} {1} on an unbound shader {2}", name, values, m_fileHandle.getFileName());
	}

	public void setUniform(String name, float value) {
		if (m_state == State.BOUND) {
			int location = getUniformLocation(name);
			GL30.glUniform1f(location, value);
			return;
		}
		LOGGER.warn("Attempted to setUniform(float) {0} {1} on an unbound shader {2}", name, value, m_fileHandle.getFileName());
	}

	public void setUniform(String name, float[] values) {
		if (m_state == State.BOUND) {
			int location = getUniformLocation(name);
			GL30.glUniform1fv(location, values);
			return;
		}
		LOGGER.warn("Attempted to setUniform(float[]) {0} {1} on an unbound shader {2}", name, values, m_fileHandle.getFileName());
	}

	public void setUniform(String name, Vector2f value) {
		if (m_state == State.BOUND) {
			int location = getUniformLocation(name);
			GL30.glUniform2f(location, value.x, value.y);
			return;
		}
		LOGGER.warn("Attempted to setUniform(Vector2f) {0} {1} on an unbound shader {2}", name, value, m_fileHandle.getFileName());
	}

	public void setUniform(String name, Vector3f value) {
		if (m_state == State.BOUND) {
			int location = getUniformLocation(name);
			GL30.glUniform3f(location, value.x, value.y, value.z);
			return;
		}
		LOGGER.warn("Attempted to setUniform(Vector3f) {0} {1} on an unbound shader {2}", name, value, m_fileHandle.getFileName());
	}

	public void setUniform(String name, Vector4f value) {
		if (m_state == State.BOUND) {
			int location = getUniformLocation(name);
			GL30.glUniform4f(location, value.x, value.y, value.z, value.w);
			return;
		}
		LOGGER.warn("Attempted to setUniform(Vector4f) {0} {1} on an unbound shader {2}", name, value, m_fileHandle.getFileName());
	}

	public void setUniform(String name, Matrix2f value) {
		if (m_state == State.BOUND) {
			int location = getUniformLocation(name);
			FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
			value.get(buffer);
			GL30.glUniformMatrix2fv(location, false, buffer);
			return;
		}
		LOGGER.warn("Attempted to setUniform(Matrix4f) {0} {1} on an unbound shader {2}", name, value, m_fileHandle.getFileName());
	}

	public void setUniform(String name, Matrix3f value) {
		if (m_state == State.BOUND) {
			int location = getUniformLocation(name);
			FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
			value.get(buffer);
			GL30.glUniformMatrix3fv(location, false, buffer);
			return;
		}
		LOGGER.warn("Attempted to setUniform(Matrix2f) {0} {1} on an unbound shader {2}", name, value, m_fileHandle.getFileName());
	}

	public void setUniform(String name, Matrix4f value) {
		if (m_state == State.BOUND) {
			int location = getUniformLocation(name);
			FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
			value.get(buffer);
			GL30.glUniformMatrix4fv(location, false, buffer);
			return;
		}
		LOGGER.warn("Attempted to setUniform(Matrix4f) {0} {1} on an unbound shader {2}", name, value, m_fileHandle.getFileName());
	}

	public void setTexture(String name, int textureSlot) {
		if (m_state == State.BOUND) {
			int location = getUniformLocation(name);
			GL30.glUniform1i(location, textureSlot);
			return;
		}
		LOGGER.warn("Attempted to setTexture(slot) {0} {1} on an unbound shader {2}", name, textureSlot, m_fileHandle.getFileName());
	}

	private int getUniformLocation(String name) {
		var location = m_uniformLocations.get(name);
		if (location == null) {
			location = GL30.glGetUniformLocation(m_programID, name);
			m_uniformLocations.put(name, location);
		}
		return location;
	}

	public FileHandle getFileHandle() {
		return m_fileHandle;
	}

	public String getVertexSrc() {
		return m_vertexSrc;
	}

	public String getFragmentSrc() {
		return m_fragmentSrc;
	}
}