package me.marlon.gfx;

import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryUtil.*;

import me.marlon.ecs.Terrain;
import org.joml.AABBf;
import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class TerrainChunk implements AutoCloseable {
    private int vao;
    private int vbo;
    private int count;
    private AABBf bounds;

    public TerrainChunk(Terrain terrain, int xMin, int yMin, int xMax, int yMax) {
        bounds = new AABBf();

        List<Vector3f> positions = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Float> altitudes = new ArrayList<>();

        for (int i = xMin; i < xMax; ++i) {
            for (int j = yMin; j < yMax; ++j) {
                Vector3f p0 = new Vector3f(i * Terrain.TILE_SIZE,                     0.0f, j * Terrain.TILE_SIZE + Terrain.TILE_SIZE);
                Vector3f p1 = new Vector3f(i * Terrain.TILE_SIZE + Terrain.TILE_SIZE, 0.0f, j * Terrain.TILE_SIZE + Terrain.TILE_SIZE);
                Vector3f p2 = new Vector3f(i * Terrain.TILE_SIZE + Terrain.TILE_SIZE, 0.0f, j * Terrain.TILE_SIZE);
                Vector3f p3 = new Vector3f(i * Terrain.TILE_SIZE,                     0.0f, j * Terrain.TILE_SIZE);

                p0.y = terrain.sampleHeight(p0.x, p0.z);
                p1.y = terrain.sampleHeight(p1.x, p1.z);
                p2.y = terrain.sampleHeight(p2.x, p2.z);
                p3.y = terrain.sampleHeight(p3.x, p3.z);

                bounds.union(p0);
                bounds.union(p1);
                bounds.union(p2);
                bounds.union(p3);

                float y = (p0.y + p1.y + p2.y + p3.y) / 4.0f;
                if (Math.abs((p0.y + p2.y) * 0.5f - y) <= Math.abs((p1.y + p3.y) * 0.5f - y)) {
                    Vector3f n0 = p1.sub(p0, new Vector3f()).cross(p2.sub(p0, new Vector3f())).normalize();
                    Vector3f n1 = p2.sub(p0, new Vector3f()).cross(p3.sub(p0, new Vector3f())).normalize();
                    float a0 = (Math.min(Math.min(p0.y, p1.y), p2.y) + Math.max(Math.max(p0.y, p1.y), p2.y)) * 0.5f;
                    float a1 = (Math.min(Math.min(p2.y, p3.y), p0.y) + Math.max(Math.max(p2.y, p3.y), p0.y)) * 0.5f;

                    positions.add(p0);
                    positions.add(p1);
                    positions.add(p2);

                    normals.add(n0);
                    normals.add(n0);
                    normals.add(n0);

                    altitudes.add(a0);
                    altitudes.add(a0);
                    altitudes.add(a0);

                    positions.add(p2);
                    positions.add(p3);
                    positions.add(p0);

                    normals.add(n1);
                    normals.add(n1);
                    normals.add(n1);

                    altitudes.add(a1);
                    altitudes.add(a1);
                    altitudes.add(a1);
                } else {
                    Vector3f n0 = p0.sub(p3, new Vector3f()).cross(p1.sub(p3, new Vector3f())).normalize();
                    Vector3f n1 = p2.sub(p1, new Vector3f()).cross(p3.sub(p1, new Vector3f())).normalize();
                    float a0 = (Math.min(Math.min(p3.y, p0.y), p1.y) + Math.max(Math.max(p3.y, p0.y), p1.y)) * 0.5f;
                    float a1 = (Math.min(Math.min(p1.y, p2.y), p3.y) + Math.max(Math.max(p1.y, p2.y), p3.y)) * 0.5f;
//                    float a0 = (p3.y + p0.y + p1.y) / 3.0f;
//                    float a1 = (p1.y + p2.y + p3.y) / 3.0f;

                    positions.add(p3);
                    positions.add(p0);
                    positions.add(p1);

                    normals.add(n0);
                    normals.add(n0);
                    normals.add(n0);

                    altitudes.add(a0);
                    altitudes.add(a0);
                    altitudes.add(a0);

                    positions.add(p1);
                    positions.add(p2);
                    positions.add(p3);

                    normals.add(n1);
                    normals.add(n1);
                    normals.add(n1);

                    altitudes.add(a1);
                    altitudes.add(a1);
                    altitudes.add(a1);
                }
            }
        }

        count = positions.size();
        FloatBuffer buffer = memAllocFloat(positions.size() * 3 + normals.size() * 3 + altitudes.size());

        for (int i = 0; i < positions.size(); ++i) {
            positions.get(i).get(i * 7, buffer);
            normals.get(i).get(i * 7 + 3, buffer);
            buffer.put(i * 7 + 6, altitudes.get(i));
        }

        vao = glCreateVertexArrays();

        vbo = glCreateBuffers();
        glNamedBufferStorage(vbo, buffer, 0);
        glVertexArrayVertexBuffer(vao, 0, vbo, 0, 28);

        memFree(buffer);

        glEnableVertexArrayAttrib(vao, 0);
        glVertexArrayAttribFormat(vao, 0, 3, GL_FLOAT, false, 0);
        glVertexArrayAttribBinding(vao, 0, 0);

        glEnableVertexArrayAttrib(vao, 1);
        glVertexArrayAttribFormat(vao, 1, 3, GL_FLOAT, false, 12);
        glVertexArrayAttribBinding(vao, 1, 0);

        glEnableVertexArrayAttrib(vao, 2);
        glVertexArrayAttribFormat(vao, 2, 1, GL_FLOAT, false, 24);
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

    public AABBf getBounds() {
        return bounds;
    }
}
