package me.marlon.gfx;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Renderer implements AutoCloseable {
    private UniformBuffer frameBlock;
    private ByteBuffer frameData;

    private Shader meshShader;
    private Shader terrainShader;

    private ArrayList<MeshInstance> queue;
    private Terrain terrain;
    private Matrix4f view;
    private Matrix4f viewInv;
    private Matrix4f proj;
    private Matrix4f projInv;

    public Renderer() {
        frameBlock = new UniformBuffer(256);
        frameData = memAlloc(frameBlock.getSize());

        meshShader = new Shader();
        terrainShader = new Shader();

        try {
            meshShader.setVertText(Files.readString(Paths.get("res/shaders/shader.vert")));
            meshShader.setFragText(Files.readString(Paths.get("res/shaders/shader.frag")));

            terrainShader.setVertText(Files.readString(Paths.get("res/shaders/terrain.vert")));
            terrainShader.setFragText(Files.readString(Paths.get("res/shaders/terrain.frag")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        meshShader.compile();
        terrainShader.compile();

        queue = new ArrayList<>();
        view = new Matrix4f();
        viewInv = new Matrix4f();
        proj = new Matrix4f();
        projInv = new Matrix4f();

        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_FRAMEBUFFER_SRGB);
    }

    public void close() {
        frameBlock.close();
        memFree(frameData);

        meshShader.close();
        terrainShader.close();
    }

    public void clear() {
        queue.clear();
        terrain = null;
        view.identity();
        viewInv.identity();
        proj.identity();
        projInv.identity();
    }

    public void draw(MeshInstance mesh) {
        queue.add(mesh);
    }

    public void submitData() {
        view.get(0, frameData);
        viewInv.get(64, frameData);
        proj.get(128, frameData);
        projInv.get(192, frameData);
        frameBlock.buffer(frameData);
        frameBlock.bind(0);

        // terrainShader.set("camera", viewInv.getTranslation(new Vector3f()));
    }

    public void submitDraw() {
        glClearColor(0.7f, 0.5f, 0.5f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        meshShader.bind();

        for (int i = 0; i < queue.size(); ++i) {
            MeshInstance instance = queue.get(i);
            meshShader.set("model", instance.matrix);

            Primitive[] primitives = instance.mesh.getPrimitives();
            for (int j = 0; j < primitives.length; ++j)
                primitives[j].draw();
        }

        if (terrain != null) {
            terrainShader.bind();
            terrain.draw();
        }
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public Matrix4f getView() {
        return view;
    }

    public void setView(Matrix4f m) {
        view.set(m);
    }

    public Matrix4f getViewInv() {
        return viewInv;
    }

    public void setViewInv(Matrix4f m) {
        viewInv.set(m);
    }

    public Matrix4f getProj() {
        return proj;
    }

    public void setProj(Matrix4f m) {
        proj.set(m);
    }

    public Matrix4f getProjInv() {
        return projInv;
    }

    public void setProjInv(Matrix4f m) {
        projInv.set(m);
    }
}
