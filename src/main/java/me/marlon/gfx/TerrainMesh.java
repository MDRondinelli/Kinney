package me.marlon.gfx;

import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryUtil.*;

import me.marlon.ecs.Terrain;
import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class TerrainMesh implements AutoCloseable {
    private int vao;
    private int vbo;
    private int count;

    public TerrainMesh(Terrain terrain) {
        ArrayList<Vector3f> vertices = new ArrayList<>();

        for (int i = 0; i < terrain.getSize() / Terrain.TILE_SIZE - 1; ++i) {
            for (int j = 0; j < terrain.getSize() / Terrain.TILE_SIZE - 1; ++j) {
                Vector3f p0 = new Vector3f(i * Terrain.TILE_SIZE, 0.0f, j * Terrain.TILE_SIZE + Terrain.TILE_SIZE);
                Vector3f p1 = new Vector3f(i * Terrain.TILE_SIZE + Terrain.TILE_SIZE, 0.0f, j * Terrain.TILE_SIZE + Terrain.TILE_SIZE);
                Vector3f p2 = new Vector3f(i * Terrain.TILE_SIZE + Terrain.TILE_SIZE, 0.0f, j * Terrain.TILE_SIZE);
                Vector3f p3 = new Vector3f(i * Terrain.TILE_SIZE, 0.0f, j * Terrain.TILE_SIZE);

                p0.y = terrain.sampleHeight(p0.x, p0.z);
                p1.y = terrain.sampleHeight(p1.x, p1.z);
                p2.y = terrain.sampleHeight(p2.x, p2.z);
                p3.y = terrain.sampleHeight(p3.x, p3.z);

                Vector3f n0 = p1.sub(p0, new Vector3f()).cross(p2.sub(p0, new Vector3f())).normalize();
                Vector3f n1 = p2.sub(p0, new Vector3f()).cross(p3.sub(p0, new Vector3f())).normalize();

                Vector3f c = new Vector3f(1.0f);

                int tile = terrain.sampleTile((i + 0.5f) * Terrain.TILE_SIZE, (j + 0.5f) * Terrain.TILE_SIZE);
                if (tile == Terrain.TILE_SNOW) {
                    c.set(1.0f, 0.95f, 0.95f);

                    Vector3f p4 = new Vector3f(p0);
                    Vector3f p5 = new Vector3f(p1);
                    Vector3f p6 = new Vector3f(p2);
                    Vector3f p7 = new Vector3f(p3);

                    p0.y += 0.25f;
                    p1.y += 0.25f;
                    p2.y += 0.25f;
                    p3.y += 0.25f;

                    vertices.add(p4);
                    vertices.add(new Vector3f(0.0f, 0.0f, 1.0f));
                    vertices.add(c);

                    vertices.add(p5);
                    vertices.add(new Vector3f(0.0f, 0.0f, 1.0f));
                    vertices.add(c);

                    vertices.add(p1);
                    vertices.add(new Vector3f(0.0f, 0.0f, 1.0f));
                    vertices.add(c);

                    vertices.add(p1);
                    vertices.add(new Vector3f(0.0f, 0.0f, 1.0f));
                    vertices.add(c);

                    vertices.add(p0);
                    vertices.add(new Vector3f(0.0f, 0.0f, 1.0f));
                    vertices.add(c);

                    vertices.add(p4);
                    vertices.add(new Vector3f(0.0f, 0.0f, 1.0f));
                    vertices.add(c);

                    vertices.add(p5);
                    vertices.add(new Vector3f(1.0f, 0.0f, 0.0f));
                    vertices.add(c);

                    vertices.add(p6);
                    vertices.add(new Vector3f(1.0f, 0.0f, 0.0f));
                    vertices.add(c);

                    vertices.add(p2);
                    vertices.add(new Vector3f(1.0f, 0.0f, 0.0f));
                    vertices.add(c);

                    vertices.add(p2);
                    vertices.add(new Vector3f(1.0f, 0.0f, 0.0f));
                    vertices.add(c);

                    vertices.add(p1);
                    vertices.add(new Vector3f(1.0f, 0.0f, 0.0f));
                    vertices.add(c);

                    vertices.add(p5);
                    vertices.add(new Vector3f(1.0f, 0.0f, 0.0f));
                    vertices.add(c);

                    vertices.add(p6);
                    vertices.add(new Vector3f(0.0f, 0.0f, -1.0f));
                    vertices.add(c);

                    vertices.add(p7);
                    vertices.add(new Vector3f(0.0f, 0.0f, -1.0f));
                    vertices.add(c);

                    vertices.add(p3);
                    vertices.add(new Vector3f(0.0f, 0.0f, -1.0f));
                    vertices.add(c);

                    vertices.add(p3);
                    vertices.add(new Vector3f(0.0f, 0.0f, -1.0f));
                    vertices.add(c);

                    vertices.add(p2);
                    vertices.add(new Vector3f(0.0f, 0.0f, -1.0f));
                    vertices.add(c);

                    vertices.add(p6);
                    vertices.add(new Vector3f(0.0f, 0.0f, -1.0f));
                    vertices.add(c);

                    vertices.add(p7);
                    vertices.add(new Vector3f(-1.0f, 0.0f, 0.0f));
                    vertices.add(c);

                    vertices.add(p4);
                    vertices.add(new Vector3f(-1.0f, 0.0f, 0.0f));
                    vertices.add(c);

                    vertices.add(p0);
                    vertices.add(new Vector3f(-1.0f, 0.0f, 0.0f));
                    vertices.add(c);

                    vertices.add(p0);
                    vertices.add(new Vector3f(-1.0f, 0.0f, 0.0f));
                    vertices.add(c);

                    vertices.add(p3);
                    vertices.add(new Vector3f(-1.0f, 0.0f, 0.0f));
                    vertices.add(c);

                    vertices.add(p7);
                    vertices.add(new Vector3f(-1.0f, 0.0f, 0.0f));
                    vertices.add(c);
                }
                else if (tile == Terrain.TILE_STONE)
                    c.set(0.4f, 0.35f, 0.35f);
                else if (tile == Terrain.TILE_GRASS) {
                    c.set(0.4f, 0.6f, 0.05f);

                    Vector3f p4 = new Vector3f(p0);
                    Vector3f p5 = new Vector3f(p1);
                    Vector3f p6 = new Vector3f(p2);
                    Vector3f p7 = new Vector3f(p3);

                    p0.y += 0.25f;
                    p1.y += 0.25f;
                    p2.y += 0.25f;
                    p3.y += 0.25f;

                    vertices.add(p4);
                    vertices.add(new Vector3f(0.0f, 0.0f, 1.0f));
                    vertices.add(c);

                    vertices.add(p5);
                    vertices.add(new Vector3f(0.0f, 0.0f, 1.0f));
                    vertices.add(c);

                    vertices.add(p1);
                    vertices.add(new Vector3f(0.0f, 0.0f, 1.0f));
                    vertices.add(c);

                    vertices.add(p1);
                    vertices.add(new Vector3f(0.0f, 0.0f, 1.0f));
                    vertices.add(c);

                    vertices.add(p0);
                    vertices.add(new Vector3f(0.0f, 0.0f, 1.0f));
                    vertices.add(c);

                    vertices.add(p4);
                    vertices.add(new Vector3f(0.0f, 0.0f, 1.0f));
                    vertices.add(c);

                    vertices.add(p5);
                    vertices.add(new Vector3f(1.0f, 0.0f, 0.0f));
                    vertices.add(c);

                    vertices.add(p6);
                    vertices.add(new Vector3f(1.0f, 0.0f, 0.0f));
                    vertices.add(c);

                    vertices.add(p2);
                    vertices.add(new Vector3f(1.0f, 0.0f, 0.0f));
                    vertices.add(c);

                    vertices.add(p2);
                    vertices.add(new Vector3f(1.0f, 0.0f, 0.0f));
                    vertices.add(c);

                    vertices.add(p1);
                    vertices.add(new Vector3f(1.0f, 0.0f, 0.0f));
                    vertices.add(c);

                    vertices.add(p5);
                    vertices.add(new Vector3f(1.0f, 0.0f, 0.0f));
                    vertices.add(c);

                    vertices.add(p6);
                    vertices.add(new Vector3f(0.0f, 0.0f, -1.0f));
                    vertices.add(c);

                    vertices.add(p7);
                    vertices.add(new Vector3f(0.0f, 0.0f, -1.0f));
                    vertices.add(c);

                    vertices.add(p3);
                    vertices.add(new Vector3f(0.0f, 0.0f, -1.0f));
                    vertices.add(c);

                    vertices.add(p3);
                    vertices.add(new Vector3f(0.0f, 0.0f, -1.0f));
                    vertices.add(c);

                    vertices.add(p2);
                    vertices.add(new Vector3f(0.0f, 0.0f, -1.0f));
                    vertices.add(c);

                    vertices.add(p6);
                    vertices.add(new Vector3f(0.0f, 0.0f, -1.0f));
                    vertices.add(c);

                    vertices.add(p7);
                    vertices.add(new Vector3f(-1.0f, 0.0f, 0.0f));
                    vertices.add(c);

                    vertices.add(p4);
                    vertices.add(new Vector3f(-1.0f, 0.0f, 0.0f));
                    vertices.add(c);

                    vertices.add(p0);
                    vertices.add(new Vector3f(-1.0f, 0.0f, 0.0f));
                    vertices.add(c);

                    vertices.add(p0);
                    vertices.add(new Vector3f(-1.0f, 0.0f, 0.0f));
                    vertices.add(c);

                    vertices.add(p3);
                    vertices.add(new Vector3f(-1.0f, 0.0f, 0.0f));
                    vertices.add(c);

                    vertices.add(p7);
                    vertices.add(new Vector3f(-1.0f, 0.0f, 0.0f));
                    vertices.add(c);
                }
                else
                    c.set(0.76f, 0.7f, 0.5f);

                vertices.add(p0);
                vertices.add(n0);
                vertices.add(c);

                vertices.add(p1);
                vertices.add(n0);
                vertices.add(c);

                vertices.add(p2);
                vertices.add(n0);
                vertices.add(c);

                vertices.add(p2);
                vertices.add(n1);
                vertices.add(c);

                vertices.add(p3);
                vertices.add(n1);
                vertices.add(c);

                vertices.add(p0);
                vertices.add(n1);
                vertices.add(c);
            }
        }

        count = vertices.size() / 3;
        FloatBuffer buffer = memAllocFloat(vertices.size() * 3);

        for (int i = 0; i < vertices.size(); ++i)
            vertices.get(i).get(i * 3, buffer);

        vao = glCreateVertexArrays();

        vbo = glCreateBuffers();
        glNamedBufferStorage(vbo, buffer, 0);
        glVertexArrayVertexBuffer(vao, 0, vbo, 0, 36);

        memFree(buffer);

        glEnableVertexArrayAttrib(vao, 0);
        glVertexArrayAttribFormat(vao, 0, 3, GL_FLOAT, false, 0);
        glVertexArrayAttribBinding(vao, 0, 0);

        glEnableVertexArrayAttrib(vao, 1);
        glVertexArrayAttribFormat(vao, 1, 3, GL_FLOAT, false, 12);
        glVertexArrayAttribBinding(vao, 1, 0);

        glEnableVertexArrayAttrib(vao, 2);
        glVertexArrayAttribFormat(vao, 2, 3, GL_FLOAT, false, 24);
        glVertexArrayAttribBinding(vao, 2, 0);
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
