package me.marlon.gfx;

import static org.lwjgl.opengl.GL45.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Primitive implements AutoCloseable {
    private int vao;
    private int vbo;
    private int count;

    public Primitive(FloatBuffer vertices) {
        vao = glCreateVertexArrays();

        vbo = glCreateBuffers();
        glNamedBufferStorage(vbo, vertices, 0);
        glVertexArrayVertexBuffer(vao, 0, vbo, 0, 24);

        glEnableVertexArrayAttrib(vao, 0);
        glVertexArrayAttribFormat(vao, 0, 3, GL_FLOAT, false, 0);
        glVertexArrayAttribBinding(vao, 0, 0);

        glEnableVertexArrayAttrib(vao, 1);
        glVertexArrayAttribFormat(vao, 1, 3, GL_FLOAT, false, 12);
        glVertexArrayAttribBinding(vao, 1, 0);

        count = vertices.capacity() / 6;
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
