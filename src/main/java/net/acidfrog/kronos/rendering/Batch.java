package net.acidfrog.kronos.rendering;

import net.acidfrog.kronos.core.assets.AssetManager;
import net.acidfrog.kronos.core.datastructure.array.Array;
import net.acidfrog.kronos.core.datastructure.array.DynamicArray;
import net.acidfrog.kronos.core.util.Reference;
import net.acidfrog.kronos.math.Vector2f;
import net.acidfrog.kronos.math.Vector4f;
import net.acidfrog.kronos.rendering.component.SpriteRendererComponent;
import net.acidfrog.kronos.scene.SceneManager;
import net.acidfrog.kronos.scene.component.TransformComponent;
import net.acidfrog.kronos.scene.ecs.Entity;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Batch {

    private static final int POSITION_SIZE      =  2;
    private static final int COLOR_SIZE         =  4;
    private static final int UV_SIZE            =  2;
    private static final int TEXTURE_ID_SIZE    =  1;

    private static final int VERTEX_SIZE        =  POSITION_SIZE + COLOR_SIZE + UV_SIZE + TEXTURE_ID_SIZE;

    private static final int POSITION_OFFSET    =  0;
    private static final int COLOR_OFFSET       =  POSITION_OFFSET + (POSITION_SIZE * Float.BYTES);
    private static final int UV_OFFSET          =  COLOR_OFFSET    + (COLOR_SIZE    * Float.BYTES);
    private static final int TEXTURE_ID_OFFSET  =  UV_OFFSET       + (UV_SIZE       * Float.BYTES);

    private SpriteRendererComponent[] sprites;
    private TransformComponent[] transforms;

    private float[] vertices;
    private int[] textureSlots = { 0, 1, 2, 3, 4, 5, 6, 7 };
    private int spriteCount;
    private boolean isFull;

    private int vaoID, vboID;
    private int maxSprites;

    private Shader shader;

    private Array<Texture> textures;

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
        this.shader = AssetManager.get("assets/shaders/default.glsl", Shader.class);
        this.textures = new DynamicArray<Texture>();
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

        glVertexAttribPointer(0, POSITION_SIZE, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, POSITION_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, UV_SIZE, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, UV_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEXTURE_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, TEXTURE_ID_OFFSET);
        glEnableVertexAttribArray(3);
    }

    public void render() {
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        shader.bind();
        shader.uploadMatrix4("uProjection", SceneManager.getInstance().getCurrentScene().getCamera().getProjectionMatrix());
        shader.uploadMatrix4("uView",       SceneManager.getInstance().getCurrentScene().getCamera().getViewMatrix());

        for (int i = 0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1); // +1 because of the reserved texture slot
            textures.get(i).bind();
        }

        shader.uploadIntArray("uTextures", textureSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, spriteCount * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        for (int i = 0; i < textures.size(); i++) textures.get(i).unbind();

        shader.unbind();
    }

    void add(Entity entity) {
        if (isFull) return;

        Reference<SpriteRendererComponent> sprite = new Reference<SpriteRendererComponent>(sprites[spriteCount] = entity.get(SpriteRendererComponent.class));
        transforms[spriteCount] = entity.get(TransformComponent.class);

        Texture texture = sprite.get().getTexture();

        if (texture != null) {
            if (!textures.contains(texture)) textures.add(texture);
        }

        loadVertexProperties(spriteCount);

        spriteCount++;

        if (spriteCount == maxSprites) isFull = true;
    }

    private void loadVertexProperties(int index) {
        SpriteRendererComponent sprite = sprites[index];
        TransformComponent transform = transforms[index];

        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();

        Vector2f[] uvs = sprite.getUVs();

        int textureID = 0;
        if (sprite.hasTexture()) {
            for (int i = 0; i < textures.size(); i++) if (textures.get(i) == sprite.getTexture()) {
                textureID = i + 1; // 0 is reserved for no texture
                break;
            }
        }

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

            // Load UV
            vertices[offset + 6] = uvs[i].x;
            vertices[offset + 7] = uvs[i].y;

            vertices[offset + 8] = textureID;

            offset += VERTEX_SIZE;
        }
    }
    
    public boolean isFull() {
        return isFull;
    }

}
