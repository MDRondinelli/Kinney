package me.marlon.gfx;

import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryUtil.*;

import me.marlon.util.OpenSimplexNoise;
import me.marlon.util.OpenSimplexOctaves;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;

public class Terrain implements AutoCloseable {
    private int vao;
    private int vbo;
    private int count;

    public Terrain(int size) {
        count = (size - 1) * (size - 1) * 6;

        OpenSimplexOctaves noise = new OpenSimplexOctaves(40.0f, 0.5f, 2.0f,  8);

        float[][] heightmap = new float[size][size];
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
                heightmap[i][j] = (float) Math.pow(heightmap[i][j], 2.4f);

                // island "mask"
                float distance = Vector2f.distance(size * 0.5f, size * 0.5f, i, j);
                float mask = 1.0f - (float) Math.pow(Math.min(1.5f * distance / (size * 0.5f), 1.0f), 3.0f);
                heightmap[i][j] *= mask;

                heightmap[i][j] *= 40.0f;

                heightmap[i][j] = Math.max(heightmap[i][j], 3.0f);
            }
        }

        FloatBuffer buffer = memAllocFloat(count * 9);

        OpenSimplexOctaves layerNoise = new OpenSimplexOctaves(8.0f, 0.5f, 2.0f, 4);

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

                Vector3f c = new Vector3f();
                if (y > 20.0f)
                    c.set(1.0f, 0.95f, 0.95f);
                else if (y > 15.0f)
                    c.set(0.5f, 0.3f, 0.3f);
                else if (y > 4.5f)
                    c.set(0.3f, 0.6f, 0.1f);
                else if (y > 3.0f)
                    c.set(0.76f, 0.7f, 0.5f);
                else
                    c.set(0.0f, 0.4f, 1.0f);

                p0.get(buffer);
                buffer.position(buffer.position() + 3);
                n0.get(buffer);
                buffer.position(buffer.position() + 3);
                c.get(buffer);
                buffer.position(buffer.position() + 3);

                p1.get(buffer);
                buffer.position(buffer.position() + 3);
                n0.get(buffer);
                buffer.position(buffer.position() + 3);
                c.get(buffer);
                buffer.position(buffer.position() + 3);

                p2.get(buffer);
                buffer.position(buffer.position() + 3);
                n0.get(buffer);
                buffer.position(buffer.position() + 3);
                c.get(buffer);
                buffer.position(buffer.position() + 3);

                p2.get(buffer);
                buffer.position(buffer.position() + 3);
                n1.get(buffer);
                buffer.position(buffer.position() + 3);
                c.get(buffer);
                buffer.position(buffer.position() + 3);

                p3.get(buffer);
                buffer.position(buffer.position() + 3);
                n1.get(buffer);
                buffer.position(buffer.position() + 3);
                c.get(buffer);
                buffer.position(buffer.position() + 3);

                p0.get(buffer);
                buffer.position(buffer.position() + 3);
                n1.get(buffer);
                buffer.position(buffer.position() + 3);
                c.get(buffer);
                buffer.position(buffer.position() + 3);
            }
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

    public void draw() {
        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, count);
    }
}
