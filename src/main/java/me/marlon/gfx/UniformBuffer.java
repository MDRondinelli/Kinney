package me.marlon.gfx;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL45.*;

public class UniformBuffer implements AutoCloseable {
    private int size;
    private int buffer;

    public UniformBuffer(int size) {
        this.size = size;
        buffer = glCreateBuffers();
        glNamedBufferStorage(buffer, size, GL_DYNAMIC_STORAGE_BIT);
    }

    public void close() {
        glDeleteBuffers(buffer);
    }

    public void buffer(long offset, ByteBuffer data) {
        glNamedBufferSubData(buffer, offset, data);
    }

    public void buffer(ByteBuffer data) {
        glNamedBufferSubData(buffer, 0, data);
    }

    public void bind(int index) {
        glBindBufferBase(GL_UNIFORM_BUFFER, index, buffer);
    }

    public void bind(int index, long offs, long size) {
        glBindBufferRange(GL_UNIFORM_BUFFER, index, buffer, offs, size);
    }

    public int getSize() {
        return size;
    }

}
