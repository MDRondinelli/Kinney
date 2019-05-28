package me.marlon.gfx;

import org.joml.Vector3f;

import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Primitive implements AutoCloseable {
    private int vao;
    private int vbo;
    private int count;

    private Vector3f albedo;

    public Primitive(String path, Vector3f albedo) throws IOException {
        this.albedo = albedo;

        ArrayList<Vector3f> vertices = new ArrayList<>();
        ArrayList<Vector3f> vertexData = new ArrayList<>();

        try (BufferedReader in = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("v ")) {
                    String[] splitted = line.split(" ");
                    float x = Float.parseFloat(splitted[1]);
                    float y = Float.parseFloat(splitted[2]);
                    float z = Float.parseFloat(splitted[3]);
                    vertices.add(new Vector3f(x, y, z));
                } else if (line.startsWith("f ")) {
                    String[] splitted = line.split(" ");
                    int v1 = Integer.parseInt(splitted[1].split("/")[0]);
                    int v2 = Integer.parseInt(splitted[2].split("/")[0]);
                    int v3 = Integer.parseInt(splitted[3].split("/")[0]);
                    Vector3f p1 = vertices.get(v1 - 1);
                    Vector3f p2 = vertices.get(v2 - 1);
                    Vector3f p3 = vertices.get(v3 - 1);
                    Vector3f n = p2.sub(p1, new Vector3f()).cross(p3.sub(p1, new Vector3f())).normalize();

                    vertexData.add(p1);
                    vertexData.add(n);
                    vertexData.add(p2);
                    vertexData.add(n);
                    vertexData.add(p3);
                    vertexData.add(n);
                }
            }
        }

        FloatBuffer buffer = memAllocFloat(vertexData.size() * 3);

        for (int i = 0; i < vertexData.size(); ++i)
            vertexData.get(i).get(i * 3, buffer);

        init(buffer);
        memFree(buffer);
    }

    public Primitive(FloatBuffer vertices, Vector3f albedo) {
        this.albedo = albedo;
        init(vertices);
    }

    private void init(FloatBuffer vertices) {
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

    public Vector3f getAlbedo() {
        return albedo;
    }

    public void setAlbedo(Vector3f albedo) {
        this.albedo = albedo;
    }
}
