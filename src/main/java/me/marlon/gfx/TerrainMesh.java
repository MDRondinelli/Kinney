package me.marlon.gfx;

import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryUtil.*;

import me.marlon.game.ErosionParticle;
import me.marlon.util.OpenSimplexOctaves;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class TerrainMesh implements AutoCloseable {
    public static final int TILE_SIZE = 2;
    public static final float WATER_ALTITUDE = 4.0f;

    private int size;
    private float[][] heightmap;

    private int vao;
    private int vbo;
    private int count;

    public TerrainMesh(int size) {
        this.size = size;
        heightmap = new float[size][size];

        OpenSimplexOctaves noise = new OpenSimplexOctaves(48.0f, 0.5f, 2.0f,  16);

        float minHeight = Float.MAX_VALUE;
        float maxHeight = Float.MIN_VALUE;
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                heightmap[i][j] = noise.eval(i, j);
                minHeight = Math.min(minHeight, heightmap[i][j]);
                maxHeight = Math.max(maxHeight, heightmap[i][j]);
            }
        }

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                heightmap[i][j] = (heightmap[i][j] - minHeight) / (maxHeight - minHeight);
                float distance = Vector2f.distance(size * 0.5f, size * 0.5f, i, j);
//                float mask = (float) Math.pow(Math.min(1.5f * distance / (size * 0.5f), 1.0f), 10.0f);
//                heightmap[i][j] -= mask;
                distance *= 3.0;
                distance /= size;

                float mask = 1.0f - (float) Math.pow(Math.min(distance, 1.0f), 2.0f);
                heightmap[i][j] *= mask;

                if (distance > 1.0f)
                    heightmap[i][j] -= (distance - 1.0f) * 0.5f;
            }
        }

        for (int i = 0; i < size * size * 8; ++i) {
            ErosionParticle erosionParticle = new ErosionParticle(new Vector2f((float) Math.random() * size, (float) Math.random() * size), 1.0f);
            for (int j = 0; j < 30; ++j)
                erosionParticle.update(this);
        }

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                 heightmap[i][j] *= 48.0f;
                 // heightmap[i][j] = Math.max(heightmap[i][j], 4.0f);
            }
        }

        OpenSimplexOctaves layerNoise = new OpenSimplexOctaves(24.0f, 0.5f, 2.0f, 4);
        ArrayList<Vector3f> vertices = new ArrayList<>();

        for (int i = 0; i < size - TILE_SIZE; i += TILE_SIZE) {
            for (int j = 0; j < size - TILE_SIZE; j += TILE_SIZE) {
                Vector3f p0 = new Vector3f(i, heightmap[i][j], -j);
                Vector3f p1 = new Vector3f(i + TILE_SIZE, heightmap[i + TILE_SIZE][j], -j);
                Vector3f p2 = new Vector3f(i + TILE_SIZE, heightmap[i + TILE_SIZE][j + TILE_SIZE], -(j + TILE_SIZE));
                Vector3f p3 = new Vector3f(i, heightmap[i][j + TILE_SIZE], -(j + TILE_SIZE));

                Vector3f n0 = p1.sub(p0, new Vector3f()).cross(p2.sub(p0, new Vector3f())).normalize();
                Vector3f n1 = p2.sub(p0, new Vector3f()).cross(p3.sub(p0, new Vector3f())).normalize();

                float y = (p0.y + p1.y + p2.y + p3.y) / 4.0f;
                if (y > 4.0f)
                    y += layerNoise.eval(i, j) * 3.0f;

                float maxY = Math.max(Math.max(Math.max(p0.y, p1.y), p2.y), p3.y);
                float minY = Math.min(Math.min(Math.min(p0.y, p1.y), p2.y), p3.y);

                Vector3f c = new Vector3f(1.0f);
                if (y > 22.0f) {
                    c.set(1.0f, 0.95f, 0.95f);

                    Vector3f p4 = new Vector3f(p0);
                    Vector3f p5 = new Vector3f(p1);
                    Vector3f p6 = new Vector3f(p2);
                    Vector3f p7 = new Vector3f(p3);

                    p0.y += 0.2f;
                    p1.y += 0.2f;
                    p2.y += 0.2f;
                    p3.y += 0.2f;

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
                else if (y > 18.0f || (maxY - minY > 1.0f && y > 12.0f))
                    c.set(0.4f, 0.35f, 0.35f);
                else if (y > 7.0f) {
                    c.set(0.4f, 0.6f, 0.1f);

                    Vector3f p4 = new Vector3f(p0);
                    Vector3f p5 = new Vector3f(p1);
                    Vector3f p6 = new Vector3f(p2);
                    Vector3f p7 = new Vector3f(p3);

                    p0.y += 0.2f;
                    p1.y += 0.2f;
                    p2.y += 0.2f;
                    p3.y += 0.2f;

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
                else //if (y != 4.0f)
                    c.set(0.76f, 0.7f, 0.5f);
//                else
//                    c.set(0.0f, 0.4f, 1.0f);

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
        FloatBuffer buffer = memAllocFloat(count * 9);

        for (Vector3f v : vertices) {
            v.get(buffer);
            buffer.position(buffer.position() + 3);
        }

        buffer.rewind();

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

    public float sample(float x, float y) {
        int x0 = Math.min(Math.max((int) Math.floor(x), 0), size - 1);
        int x1 = Math.min(Math.max((int) Math.ceil(x), 0), size - 1);
        int y0 = Math.min(Math.max((int) Math.floor(y), 0), size - 1);
        int y1 = Math.min(Math.max((int) Math.ceil(y), 0), size - 1);

        float u = Math.min(x - x0, 1.0f);
        float v = Math.min(x - x0, 1.0f);

        float l = (1.0f - v) * heightmap[x0][y0] + v * heightmap[x0][y1];
        float r = (1.0f - v) * heightmap[x1][y0] + v * heightmap[x1][y1];
        return (1.0f - u) * l + u * r;
    }

    public Vector2f gradient(float x, float y) {
        int x0 = Math.min(Math.max((int) Math.floor(x), 0), size - 1);
        int x1 = Math.min(Math.max((int) Math.ceil(x), 0), size - 1);
        int y0 = Math.min(Math.max((int) Math.floor(y), 0), size - 1);
        int y1 = Math.min(Math.max((int) Math.ceil(y), 0), size - 1);

        float u = Math.min(x - x0, 1.0f);
        float v = Math.min(x - x0, 1.0f);

        Vector2f g = new Vector2f();
        g.x = (heightmap[x1][y0] - heightmap[x0][y0]) * (1.0f - v) + (heightmap[x1][y1] - heightmap[x0][y1]) * v;
        g.y = (heightmap[x0][y1] - heightmap[x0][y0]) * (1.0f - u) + (heightmap[x1][y1] - heightmap[x1][y0]) * u;
        return g;
    }

    public void deposit(float x, float y, float amt) {
        if (x < 0.0f || x >= size - 1 || y < 0.0f || y >= size - 1)
            return;

        float u = x - (int) x;
        float v = y - (int) y;

//        float leftWeight = 1.0f - u;
//        float rightWeight = u;
//
//        float bottomWeight = 1.0f - v;
//        float topWeight = v;

        heightmap[(int) x][(int) y] += amt * (1.0f - u) * (1.0f - v);
        heightmap[(int) x][(int) y + 1] += amt * (1.0f - u) * v;

        heightmap[(int) x + 1][(int) y] += amt * u * (1.0f - v);
        heightmap[(int) x + 1][(int) y + 1] += amt * u * v;
    }

    public void erode(float x, float y, float r, float amt) {
        float normalizationFactor = 0.0f;

        for (int i = (int) (x - r); i <= (int) (x + r); ++i) {
            if (i < 0 || i >= size)
                continue;

            for (int j = (int) (y - r); j <= (int) (y + r); ++j) {
                if (j < 0 || j >= size)
                    continue;

                normalizationFactor += Math.max(0.0f, r - Vector2f.distance(i, j, x, y));
            }
        }

        for (int i = (int) (x - r); i <= (int) (x + r); ++i) {
            if (i < 0 || i >= size)
                continue;

            for (int j = (int) (y - r); j <= (int) (y + r); ++j) {
                if (j < 0 || j >= size)
                    continue;

                float w = Math.max(0.0f, r - Vector2f.distance(i, j, x, y));
                heightmap[i][j] -= amt * w / normalizationFactor;
            }
        }
    }

    public void draw() {
        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, count);
    }
}