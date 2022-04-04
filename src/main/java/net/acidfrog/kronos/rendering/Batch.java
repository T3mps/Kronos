package net.acidfrog.kronos.rendering;

import net.acidfrog.kronos.math.Vector4f;
import net.acidfrog.kronos.rendering.component.SpriteRendererComponent;
import net.acidfrog.kronos.scene.SceneManager;
import net.acidfrog.kronos.scene.component.TransformComponent;
import net.acidfrog.kronos.scene.ecs.Entity;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Batch {

    private static final int POSITION_SIZE = 2;
    private static final int COLOR_SIZE = 4;
    private static final int VERTEX_SIZE = 6;
    private static final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private static final int POSITION_OFFSET = 0;
    private static final int COLOR_OFFSET = POSITION_OFFSET + (POSITION_SIZE * Float.BYTES);

    private SpriteRendererComponent[] sprites;
    private TransformComponent[] transforms;
    private float[] vertices;
    private int spriteCount;
    private boolean isFull;

    private int vaoID, vboID;
    private int maxSprites;

    private Shader shader;

    public Batch() {
        this(Renderer2D.MAX_BATCH_SIZE);
    }

    public Batch(int maxSprites) {
        this.sprites = new SpriteRendererComponent[maxSprites];
        this.transforms = new TransformComponent[maxSprites];
        this.vertices = new float[maxSprites * 4 * VERTEX_SIZE]; // 4 vertices
        this.spriteCount = 0;
        this.isFull = false;
        this.maxSprites = maxSprites;
        this.shader = new Shader("assets/shaders/default.glsl").compile();
    }

    public void begin() {
        vaoID = glGenVertexArrays();
        vboID = glGenBuffers();
        
        glBindVertexArray(vaoID);
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        int eboID = glGenBuffers();

        int[] indices = new int[maxSprites * 6];
        int offset = 0;

        for (int i = 0; i < maxSprites; i++) {
            indices[offset++] = i * 4 + 3;
            indices[offset++] = i * 4 + 2;
            indices[offset++] = i * 4 + 0;

            indices[offset++] = i * 4 + 0;
            indices[offset++] = i * 4 + 2;
            indices[offset++] = i * 4 + 1;
        }

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, POSITION_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POSITION_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);
    }

    public void render() {
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        shader.bind();
        shader.uploadMatrix4("uProjection", SceneManager.getInstance().getCurrentScene().getCamera().getProjectionMatrix());
        shader.uploadMatrix4("uView",       SceneManager.getInstance().getCurrentScene().getCamera().getViewMatrix());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, spriteCount * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        shader.unbind();
    }

    void add(Entity entity) {
        if (isFull) return;

        sprites[spriteCount] = entity.get(SpriteRendererComponent.class);
        transforms[spriteCount] = entity.get(TransformComponent.class);
        loadVertexProperties(spriteCount);

        spriteCount++;

        if (spriteCount == maxSprites) isFull = true;
    }

    private void loadVertexProperties(int index) {
        SpriteRendererComponent sprite = sprites[index];
        TransformComponent transform = transforms[index];

        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();

        // Add vertices with the appropriate properties
        float xAdd = 1f;
        float yAdd = 1f;

        for (int i = 0; i < 4; i++) {
            if (i == 1) yAdd = 0.0f;
            if (i == 2) xAdd = 0.0f;
            if (i == 3) yAdd = 1.0f;

            // Load position
            vertices[offset + 0] = transform.getPosition().x + (xAdd * transform.getScale().x());
            vertices[offset + 1] = transform.getPosition().y + (yAdd * transform.getScale().y());

            // Load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            offset += VERTEX_SIZE;
        }
    }
    
    public boolean isFull() {
        return isFull;
    }

}
