package net.acidfrog.test;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

import net.acidfrog.kronos.core.Config;
import net.acidfrog.kronos.core.application.KronosApplication;
import net.acidfrog.kronos.renderer.Shader;
import net.acidfrog.kronos.scene.Scene;
import net.acidfrog.kronos.scene.SceneManager;

public class TestApplication extends KronosApplication {

    public TestApplication(int windowWidth, int windowHeight, String windowTitle) {
        super(windowWidth, windowHeight, windowTitle);
        SceneManager.instance.loadScene(new Scene("Test Scene") {
            @Override
            public void initialize() {
                defaultShader = new Shader(Config.SHADER_PATH + "default.glsl").load();
                
                // ============================================================
                // Generate VAO, VBO, and EBO buffer objects, and send to GPU
                // ============================================================
                vaoID = GL30.glGenVertexArrays();
                GL30.glBindVertexArray(vaoID);

                // Create a float buffer of vertices
                FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
                vertexBuffer.put(vertexArray).flip();

                // Create VBO upload the vertex buffer
                vboID = GL30.glGenBuffers();
                GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboID);
                GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertexBuffer, GL30.GL_STATIC_DRAW);

                // Create the indices and upload
                IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
                elementBuffer.put(elementArray).flip();

                eboID = GL30.glGenBuffers();
                GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, eboID);
                GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL30. GL_STATIC_DRAW);

                // Add the vertex attribute pointers
                int positionsSize = 3;
                int colorSize = 4;
                int vertexSizeBytes = (positionsSize + colorSize) * Float.BYTES;

                GL30.glVertexAttribPointer(0, positionsSize, GL30.GL_FLOAT, false, vertexSizeBytes, 0);
                GL30.glEnableVertexAttribArray(0);

                GL30.glVertexAttribPointer(1, colorSize, GL30.GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);
                GL30.glEnableVertexAttribArray(1);
            }
        }); 
        run();
    }

    @Override
    public void update(float deltaTime) {        
        super.update(deltaTime);
    }

    @Override
    public void physicsUpdate(float deltaTime) {        
        super.physicsUpdate(deltaTime);
    }

    @Override
    public void render() {
        super.render();
    }

    public static void main(String[] args) {
        new TestApplication(800, 600, "Kronos");
    }
    
}
