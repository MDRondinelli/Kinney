package me.marlon.gfx;

import org.joml.*;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL45.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class Shader implements AutoCloseable {
    private String vertText;
    private String fragText;
    private String compText;

    private int program;
    private HashMap<String, Integer> uniforms;

    public Shader() {
        program = glCreateProgram();
        uniforms = new HashMap<>();
    }

    public void close() {
        glDeleteProgram(program);
    }

    private int createShader(String src, int type) {
        int shader = glCreateShader(type);
        glShaderSource(shader, src);
        glCompileShader(shader);

        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println(glGetShaderInfoLog(shader));
            System.exit(0);
        }

        return shader;
    }

    public void compile() {
        ArrayList<Integer> shaders = new ArrayList<>();

        if (vertText != null)
            shaders.add(createShader(vertText, GL_VERTEX_SHADER));
        if (fragText != null)
            shaders.add(createShader(fragText, GL_FRAGMENT_SHADER));
        if (compText != null)
            shaders.add(createShader(compText, GL_COMPUTE_SHADER));

        for (int i = 0; i < shaders.size(); ++i)
            glAttachShader(program, shaders.get(i));

        glLinkProgram(program);

        for (int i = 0; i < shaders.size(); ++i) {
            int shader = shaders.get(i);

            glDetachShader(program, shader);
            glDeleteShader(shader);
        }

        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println(glGetProgramInfoLog(program));
            System.exit(0);
        }

        int activeUniforms = glGetProgrami(program, GL_ACTIVE_UNIFORMS);
        for (int i = 0; i < activeUniforms; ++i) {
            String name = glGetActiveUniformName(program, i);
            int location = glGetUniformLocation(program, name);
            if (location != 0xffffffff)
                uniforms.put(name, location);
        }
    }

    public void bind() {
        glUseProgram(program);
    }

    public void set(String name, float x) {
        glProgramUniform1f(program, uniforms.get(name), x);
    }

    public void set(String name, Vector2f v) {
        glProgramUniform2f(program, uniforms.get(name), v.x, v.y);
    }

    public void set(String name, Vector3f v) {
        glProgramUniform3f(program, uniforms.get(name), v.x, v.y, v.z);
    }

    public void set(String name, Vector4f v) {
        glProgramUniform4f(program, uniforms.get(name), v.x, v.y, v.z, v.w);
    }

    public void set(String name, int x) {
        glProgramUniform1i(program, uniforms.get(name), x);
    }

    public void set(String name, Vector2i v) {
        glProgramUniform2i(program, uniforms.get(name), v.x, v.y);
    }

    public void set(String name, Vector3i v) {
        glProgramUniform3i(program, uniforms.get(name), v.x, v.y, v.z);
    }

    public void set(String name, Vector4i v) {
        glProgramUniform4i(program, uniforms.get(name), v.x, v.y, v.z, v.w);
    }

    public void set(String name, Matrix3f m) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(12);
            m.get(buffer);
            glProgramUniformMatrix3fv(program, uniforms.get(name), false, buffer);
        }
    }

    public void set(String name, Matrix4f m) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            m.get(buffer);
            glProgramUniformMatrix4fv(program, uniforms.get(name), false, buffer);
        }
    }

    public String getVertText() {
        return vertText;
    }

    public void setVertText(String text) {
        vertText = text;
    }

    public String getFragText() {
        return fragText;
    }

    public void setFragText(String text) {
        fragText = text;
    }

    public String getCompText() {
        return compText;
    }

    public void setCompText(String text) {
        compText = text;
    }
}
