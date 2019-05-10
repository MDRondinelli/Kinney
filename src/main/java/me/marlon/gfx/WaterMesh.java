package me.marlon.gfx;

import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class WaterMesh implements AutoCloseable {
    public static final int TILE_SIZE = 8;

    private int vao;
    private int vbo;
    private int count;

    public WaterMesh(int size) {
        ArrayList<Vector2f> vertices = new ArrayList<>();

        for (int i = 0; i < size / TILE_SIZE; ++i) {
            for (int j = 0; j < size / TILE_SIZE; ++j) {
                float x = i * TILE_SIZE;
                float z = j * TILE_SIZE;

                int startingIdx = vertices.size();

                vertices.add(new Vector2f(x, z + TILE_SIZE));
                vertices.add(new Vector2f(TILE_SIZE, 0.0f));
                vertices.add(new Vector2f(TILE_SIZE, -TILE_SIZE));

                vertices.add(new Vector2f(x + TILE_SIZE, z + TILE_SIZE));
                vertices.add(new Vector2f(0.0f, -TILE_SIZE));
                vertices.add(new Vector2f(-TILE_SIZE, 0.0f));

                vertices.add(new Vector2f(x + TILE_SIZE, z));
                vertices.add(new Vector2f(-TILE_SIZE, TILE_SIZE));
                vertices.add(new Vector2f(0.0f, TILE_SIZE));

                vertices.add(new Vector2f(x + TILE_SIZE, z));
                vertices.add(new Vector2f(-TILE_SIZE, 0.0f));
                vertices.add(new Vector2f(-TILE_SIZE, TILE_SIZE));

                vertices.add(new Vector2f(x, z));
                vertices.add(new Vector2f(0.0f, TILE_SIZE));
                vertices.add(new Vector2f(TILE_SIZE, TILE_SIZE));

                vertices.add(new Vector2f(x, z + TILE_SIZE));
                vertices.add(new Vector2f(TILE_SIZE, -TILE_SIZE));
                vertices.add(new Vector2f(0.0f, -TILE_SIZE));
            }
        }

        count = vertices.size() / 3;

        FloatBuffer vertexBuffer = memAllocFloat(vertices.size() * 2);

        for (int i = 0; i < vertices.size(); ++i)
            vertices.get(i).get(i * 2, vertexBuffer);

        vao = glCreateVertexArrays();

        vbo = glCreateBuffers();
        glNamedBufferStorage(vbo, vertexBuffer, 0);
        glVertexArrayVertexBuffer(vao, 0, vbo, 0, 24);

        glEnableVertexArrayAttrib(vao, 0);
        glVertexArrayAttribFormat(vao, 0, 2, GL_FLOAT, false, 0);
        glVertexArrayAttribBinding(vao, 0, 0);

        glEnableVertexArrayAttrib(vao, 1);
        glVertexArrayAttribFormat(vao, 1, 2, GL_FLOAT, false, 8);
        glVertexArrayAttribBinding(vao, 1, 0);

        glEnableVertexArrayAttrib(vao, 2);
        glVertexArrayAttribFormat(vao, 2, 2, GL_FLOAT, false, 16);
        glVertexArrayAttribBinding(vao, 2, 0);

        memFree(vertexBuffer);
    }

    public void close() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
    }

    public void draw() {
        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, count);
    }
}
