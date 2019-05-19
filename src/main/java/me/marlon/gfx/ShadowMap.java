package me.marlon.gfx;

import static org.lwjgl.opengl.GL45.*;

public class ShadowMap implements AutoCloseable {
    private int size;
    private int framebuffer;
    private int texture;

    public ShadowMap(int size) {
        this.size = size;
        framebuffer = glCreateFramebuffers();

        texture = glCreateTextures(GL_TEXTURE_2D);
        glTextureStorage2D(texture, 1, GL_DEPTH_COMPONENT32F, size, size);
        glTextureParameteri(texture, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTextureParameteri(texture, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTextureParameteri(texture, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTextureParameteri(texture, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTextureParameteri(texture, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_REF_TO_TEXTURE);
        glTextureParameteri(texture, GL_TEXTURE_COMPARE_FUNC, GL_LESS);
        glNamedFramebufferTexture(framebuffer, GL_DEPTH_ATTACHMENT, texture, 0);
    }

    public void close() {
        glDeleteFramebuffers(framebuffer);
        glDeleteTextures(texture);
    }

    public void bindFramebuffer(int target) {
        glBindFramebuffer(target, framebuffer);
    }

    public void bindTexture(int unit) {
        glBindTextureUnit(unit, texture);
    }

    public int getSize() {
        return size;
    }
}
