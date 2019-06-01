package me.marlon.gfx;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.*;

public class Texture implements AutoCloseable {
    private int width;
    private int height;
    private int texture;

    public static Texture fromFile(String path, int internalFormat) {
        Texture texture;

        try (MemoryStack stack = stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer n = stack.mallocInt(1);

            if (path.endsWith(".hdr")) {
                FloatBuffer pixels = stbi_loadf(path, w, h, n, 4);
                texture = new Texture(w.get(), h.get(), internalFormat, GL_RGBA, pixels);
                stbi_image_free(pixels);
            } else {
                ByteBuffer pixels = stbi_load(path, w, h, n, 4);
                texture = new Texture(w.get(), h.get(), internalFormat, GL_RGBA, pixels);
                stbi_image_free(pixels);
            }
        }

        return texture;
    }

    public Texture(int width, int height, int internalFormat) {
        this.width = width;
        this.height = height;

        texture = glCreateTextures(GL_TEXTURE_2D);
        glTextureStorage2D(texture, 32 - Integer.numberOfLeadingZeros(Math.max(width, height)), internalFormat, width, height);

        glTextureParameteri(texture, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTextureParameteri(texture, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTextureParameteri(texture, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTextureParameteri(texture, GL_TEXTURE_WRAP_T, GL_REPEAT);
    }

    public Texture(int width, int height, int internalFormat, int format, ByteBuffer pixels) {
        this(width, height, internalFormat);
        image(format, pixels);
    }

    public Texture(int width, int height, int internalFormat, int format, FloatBuffer pixels) {
        this(width, height, internalFormat);
        image(format, pixels);
    }

    @Override
    public void close() {
        glDeleteTextures(texture);
    }

    public void image(int format, ByteBuffer pixels) {
        glTextureSubImage2D(texture, 0, 0, 0, width, height, format, GL_UNSIGNED_BYTE, pixels);
        glGenerateTextureMipmap(texture);
    }

    public void image(int format, FloatBuffer pixels) {
        glTextureSubImage2D(texture, 0, 0, 0, width, height, format, GL_FLOAT, pixels);
        glGenerateTextureMipmap(texture);
    }

    public void bind(int unit) {
        glBindTextureUnit(unit, texture);
    }

    public void bindImage(int unit, int access, int format) {
        glBindImageTexture(unit, texture, 0, false, 0, access, format);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
