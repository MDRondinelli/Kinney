package me.marlon.gfx;

import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryUtil.*;

import me.marlon.util.OpenSimplexOctaves;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Terrain implements AutoCloseable {
    private int size;
    private float[][] heightmap;

    private int vao;
    private int vbo;
    private int count;

    public Terrain(int size) {
        this.size = size;
        heightmap = new float[size][size];

        OpenSimplexOctaves noise = new OpenSimplexOctaves(42.0f, 0.5f, 1.9f,  6);

        float minHeight = Float.MAX_VALUE;
        float maxHeight = Float.MIN_VALUE;
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                heightmap[i][j] = noise.eval(i, j);
                minHeight = Math.min(minHeight, heightmap[i][j]);
                maxHeight = Math.max(maxHeight, heightmap[i][j]);
            }
        }

        float[][] blurred = new float[size][size];
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                heightmap[i][j] = (heightmap[i][j] - minHeight) / (maxHeight - minHeight);
                heightmap[i][j] = (float) Math.pow(heightmap[i][j], 2.3f);
            }
        }

        for (int i = 0; i < size; ++i)
            for (int j = 0; j < size; ++j)
                blurred[i][j] = blurSample(i, j, (1.0f - heightmap[i][j]) * 0.5f);

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                heightmap[i][j] = blurred[i][j];
                float distance = Vector2f.distance(size * 0.5f, size * 0.5f, i, j);
                float mask = 1.0f - (float) Math.pow(Math.min(1.5f * distance / (size * 0.5f), 1.0f), 3.0f);
                heightmap[i][j] *= mask;
                heightmap[i][j] *= 40.0f;
                heightmap[i][j] = Math.max(heightmap[i][j], 3.0f);
            }
        }

        OpenSimplexOctaves layerNoise = new OpenSimplexOctaves(8.0f, 0.5f, 2.0f, 4);
        ArrayList<Vector3f> vertices = new ArrayList<>();

        for (int i = 0; i < size - 1; ++i) {
            for (int j = 0; j < size - 1; ++j) {
                Vector3f p0 = new Vector3f(i, heightmap[i][j], -j);
                Vector3f p1 = new Vector3f(i + 1, heightmap[i + 1][j], -j);
                Vector3f p2 = new Vector3f(i + 1, heightmap[i + 1][j + 1], -(j + 1));
                Vector3f p3 = new Vector3f(i, heightmap[i][j + 1], -(j + 1));

                Vector3f n0 = p1.sub(p0, new Vector3f()).cross(p2.sub(p0, new Vector3f())).normalize();
                Vector3f n1 = p2.sub(p0, new Vector3f()).cross(p3.sub(p0, new Vector3f())).normalize();

                float y = (p0.y + p1.y + p2.y + p3.y) / 4.0f;
                if (y > 3.0f)
                    y += layerNoise.eval(i, j) * 2.0f;

                float maxY = Math.max(Math.max(Math.max(p0.y, p1.y), p2.y), p3.y);
                float minY = Math.min(Math.min(Math.min(p0.y, p1.y), p2.y), p3.y);

                Vector3f c = new Vector3f();
                if (y > 20.0f) {
                    c.set(1.0f, 0.95f, 0.95f);

                    Vector3f p4 = new Vector3f(p0);
                    Vector3f p5 = new Vector3f(p1);
                    Vector3f p6 = new Vector3f(p2);
                    Vector3f p7 = new Vector3f(p3);

                    p0.y += 0.3f;
                    p1.y += 0.3f;
                    p2.y += 0.3f;
                    p3.y += 0.3f;

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
                else if (y > 16.0f || maxY - minY > 1.7f)
                    c.set(0.4f, 0.35f, 0.35f);
                else if (y > 5.0f) {
                    c.set(0.4f, 0.6f, 0.1f);

                    Vector3f p4 = new Vector3f(p0);
                    Vector3f p5 = new Vector3f(p1);
                    Vector3f p6 = new Vector3f(p2);
                    Vector3f p7 = new Vector3f(p3);

                    p0.y += 0.3f;
                    p1.y += 0.3f;
                    p2.y += 0.3f;
                    p3.y += 0.3f;

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
                else if (y != 3.0f)
                    c.set(0.76f, 0.7f, 0.5f);
                else
                    c.set(0.0f, 0.4f, 1.0f);

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

    private float sample(float x, float y) {
        int x0 = Math.min(Math.max((int) Math.floor(x), 0), size - 1);
        int x1 = Math.min(Math.max((int) Math.ceil(x), 0), size - 1);
        int y0 = Math.min(Math.max((int) Math.floor(y), 0), size - 1);
        int y1 = Math.min(Math.max((int) Math.ceil(y), 0), size - 1);

        float tx = Math.min(x - x0, 1.0f);
        float ty = Math.min(x - x0, 1.0f);

        float l = (1.0f - ty) * heightmap[x0][y0] + ty * heightmap[x0][y1];
        float r = (1.0f - ty) * heightmap[x1][y0] + ty * heightmap[x1][y1];
        return (1.0f - tx) * l + tx * r;
    }

    private float blurSample(float x, float y, float r) {
        float sum = 0.0f;
        sum += sample(x - r, y - r) / 9.0f;
        sum += sample(x - r, y) / 9.0f;
        sum += sample(x - r, y + r) / 9.0f;
        sum += sample(x, y - r) / 9.0f;
        sum += sample(x, y) / 9.0f;
        sum += sample(x, y + r) / 9.0f;
        sum += sample(x + r, y - r) / 9.0f;
        sum += sample(x + r, y) / 9.0f;
        sum += sample(x + r, y + r) / 9.0f;
        return sum;
    }

    public void draw() {
        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, count);
    }
}
