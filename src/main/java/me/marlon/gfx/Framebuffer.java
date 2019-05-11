package me.marlon.gfx;

import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL45.*;

public class Framebuffer implements AutoCloseable {
    private int width;
    private int height;
    private int framebuffer;
    private List<Integer> textures;

    public Framebuffer(int width, int height, int[] formats) {
        this.width = width;
        this.height = height;
        this.framebuffer = glCreateFramebuffers();
        this.textures = new ArrayList<>(formats.length + 1);

        int depth = glCreateTextures(GL_TEXTURE_2D);
        glTextureStorage2D(depth, 1, GL_DEPTH_COMPONENT32F, width, height);
        glTextureParameteri(depth, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTextureParameteri(depth, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTextureParameteri(depth, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTextureParameteri(depth, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glNamedFramebufferTexture(framebuffer, GL_DEPTH_ATTACHMENT, depth, 0);
        textures.add(depth);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer drawBuffers = stack.mallocInt(formats.length);

            for (int i = 0; i < formats.length; i++) {
                int texture = glCreateTextures(GL_TEXTURE_2D);
                glTextureStorage2D(texture, 1, formats[i], width, height);
                glTextureParameteri(texture, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                glTextureParameteri(texture, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                glTextureParameteri(texture, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                glTextureParameteri(texture, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

                int drawBuffer = GL_COLOR_ATTACHMENT0 + i;
                drawBuffers.put(i, drawBuffer);

                glNamedFramebufferTexture(framebuffer, drawBuffer, texture, 0);
                textures.add(texture);
            }

            glNamedFramebufferDrawBuffers(framebuffer, drawBuffers);
        }
    }

    public void close() {
        glDeleteFramebuffers(framebuffer);

        for (int i = 0; i < textures.size(); i++)
            glDeleteTextures(textures.get(i));
    }

    public void bind(int target) {
        glBindFramebuffer(target, framebuffer);
    }

    public void bindTexture(int index, int unit) {
        glBindTextureUnit(unit, textures.get(index));
    }

    public void bindImage(int index, int unit, int access, int format) {
        glBindImageTexture(unit, textures.get(index), 0, false, 0, access, format);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTexture(int index) {
        return textures.get(index);
    }

    public int getHandle() {
        return framebuffer;
    }
}
