package net.acidfrog.kronos.scene;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.acidfrog.kronos.core.lang.logger.Logger;
import net.acidfrog.kronos.math.Vector2;
import net.acidfrog.kronos.renderer.Camera;
import net.acidfrog.kronos.renderer.Shader;

public class Scene {
    
    // temporary
    public int vertexID, fragmentID, shaderProgram;

    public float[] vertexArray = {
        // position               // color
         1f, -1f, 0.0f,       1.0f, 0.0f, 0.0f, 1.0f, // Bottom right 0
        -1f,  1f, 0.0f,       0.0f, 1.0f, 0.0f, 1.0f, // Top left     1
         1f,  1f, 0.0f ,      1.0f, 0.0f, 1.0f, 1.0f, // Top right    2
        -1f, -1f, 0.0f,       1.0f, 1.0f, 0.0f, 1.0f, // Bottom left  3
    };

    // IMPORTANT: Must be in counter-clockwise order
    public int[] elementArray = {
            /*
                    x        x
                    x        x
             */
            2, 1, 0, // Top right triangle
            0, 1, 3 // bottom left triangle
    };

    public int vaoID, vboID, eboID;
    
    public Shader defaultShader;

    // end temporary

    private final String sceneName;
    private int sceneIndex;
    private Camera camera;

    public Scene(String name) {
        this.sceneName = name;
        this.camera = new Camera(Vector2.ZERO);
    }

    public void initialize() {
        Logger.instance.logWarn("Scene[" + sceneIndex + "] '" + sceneName + "' is not implemented yet.");
    }

    public void update(float dt) {
    }

    public void physicsUpdate(float pdt) {
    }

    public void render() {
        // temporary
        defaultShader.bind();
        defaultShader.uploadUniform("uProjMat", camera.getProjectionMatrix());
        defaultShader.uploadUniform("uViewMat", camera.getViewMatrix());

        GL30.glBindVertexArray(vaoID);

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        
        GL20.glDrawElements(GL30.GL_TRIANGLES, elementArray.length, GL30.GL_UNSIGNED_INT, 0);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);

        GL30.glBindVertexArray(0);
        
        defaultShader.unbind();
    }

    public void close() {
    }

    public String getName() {
        return sceneName;
    }

    public int getIndex() {
        return sceneIndex;
    }
    
    int setIndex(int index) {
        this.sceneIndex = index;
        return index;
    }

}
