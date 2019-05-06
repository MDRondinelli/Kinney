package me.marlon.gfx;

import static org.lwjgl.opengl.GL45.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Primitive implements AutoCloseable {
    private int vao;
    private int vbo;
    private int ibo;
    private int count;

    public Primitive(FloatBuffer vertices, IntBuffer indices) {
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

        ibo = glCreateBuffers();
        glNamedBufferStorage(ibo, indices, 0);
        glVertexArrayElementBuffer(vbo, ibo);

        count = indices.capacity();
    }

    public void close() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(vao);
    }

    public void draw() {
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, 0);
    }
}
