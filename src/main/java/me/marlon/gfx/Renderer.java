package me.marlon.gfx;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Renderer implements AutoCloseable {
    private Framebuffer gbuffer;
    private Framebuffer pbuffer;
    private Primitive screenMesh;

    private UniformBuffer frameBlock;
    private ByteBuffer frameData;

    private Shader lightShader;
    private Shader meshShader;
    private Shader terrainShader;
    private Shader waterShader;

    private ArrayList<MeshInstance> queue;
    private TerrainMesh terrainMesh;
    private WaterMesh waterMesh;
    private Matrix4f view;
    private Matrix4f viewInv;
    private Matrix4f proj;
    private Matrix4f projInv;
    private DirectionalLight dLight;

    public Renderer(int width, int height) {
        gbuffer = new Framebuffer(width, height, new int[] { GL_RGB8, GL_RGB10 });
        pbuffer = new Framebuffer(width, height, new int[] { GL_RGB8 });

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertices = stack.mallocFloat(3 * 6);

            vertices.put(-1.0f);
            vertices.put(-1.0f);
            vertices.put(1.0f);

            vertices.put(0.0f);
            vertices.put(0.0f);
            vertices.put(0.0f);

            vertices.put(3.0f);
            vertices.put(-1.0f);
            vertices.put(1.0f);

            vertices.put(0.0f);
            vertices.put(0.0f);
            vertices.put(0.0f);

            vertices.put(-1.0f);
            vertices.put(3.0f);
            vertices.put(1.0f);

            vertices.put(0.0f);
            vertices.put(0.0f);
            vertices.put(0.0f);

            screenMesh = new Primitive(vertices.rewind());
        }

        frameBlock = new UniformBuffer(288);
        frameData = memAlloc(frameBlock.getSize());

        lightShader = new Shader();
        meshShader = new Shader();
        terrainShader = new Shader();
        waterShader = new Shader();

        try {
            lightShader.setVertText(Files.readString(Paths.get("res/shaders/light.vert")));
            lightShader.setFragText(Files.readString(Paths.get("res/shaders/light.frag")));

            meshShader.setVertText(Files.readString(Paths.get("res/shaders/shader.vert")));
            meshShader.setFragText(Files.readString(Paths.get("res/shaders/shader.frag")));

            terrainShader.setVertText(Files.readString(Paths.get("res/shaders/terrain.vert")));
            terrainShader.setFragText(Files.readString(Paths.get("res/shaders/terrain.frag")));

            waterShader.setVertText(Files.readString(Paths.get("res/shaders/water.vert")));
            waterShader.setFragText(Files.readString(Paths.get("res/shaders/water.frag")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        lightShader.compile();
        meshShader.compile();
        terrainShader.compile();
        waterShader.compile();

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
        gbuffer.close();
        screenMesh.close();

        frameBlock.close();
        memFree(frameData);

        lightShader.close();
        meshShader.close();
        terrainShader.close();
        waterShader.close();
    }

    public void clear() {
        queue.clear();
        terrainMesh = null;
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

        if (dLight == null) {
            new Vector4f(0.0f).get(256, frameData);
            new Vector4f(0.0f).get(272, frameData);
        } else {
            dLight.color.get(256, frameData);
            dLight.direction.get(272, frameData);
        }

        frameBlock.buffer(frameData);
        frameBlock.bind(0);

        waterShader.set("time", (float) glfwGetTime());
    }

    public void submitDraw() {
        gbuffer.bind(GL_FRAMEBUFFER);
        glEnable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        meshShader.bind();

        for (int i = 0; i < queue.size(); ++i) {
            MeshInstance instance = queue.get(i);
            meshShader.set("model", instance.matrix);

            Primitive[] primitives = instance.mesh.getPrimitives();
            for (int j = 0; j < primitives.length; ++j)
                primitives[j].draw();
        }

        if (terrainMesh != null) {
            terrainShader.bind();
            terrainMesh.draw();
        }

//        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        pbuffer.bind(GL_FRAMEBUFFER);
        glDisable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT);

        lightShader.bind();
        gbuffer.bindTexture(0, 0);
        gbuffer.bindTexture(1, 1);
        gbuffer.bindTexture(2, 2);
        screenMesh.draw();

        if (waterMesh != null) {
            glBlitNamedFramebuffer(gbuffer.getHandle(), pbuffer.getHandle(),
                    0, 0, pbuffer.getWidth(), pbuffer.getHeight(),
                    0, 0, pbuffer.getWidth(), pbuffer.getHeight(),
                    GL_DEPTH_BUFFER_BIT, GL_NEAREST);

            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_DEPTH_TEST);

            waterShader.bind();
            waterMesh.draw();

            glDisable(GL_BLEND);
        }

        glBlitNamedFramebuffer(pbuffer.getHandle(), 0,
                0, 0, pbuffer.getWidth(), pbuffer.getHeight(),
                0, 0, pbuffer.getWidth(), pbuffer.getHeight(),
                GL_COLOR_BUFFER_BIT, GL_NEAREST);
    }

    public void setTerrainMesh(TerrainMesh terrainMesh) {
        this.terrainMesh = terrainMesh;
    }

    public void setWaterMesh(WaterMesh waterMesh) {
        this.waterMesh = waterMesh;
    }

    public void setView(Matrix4f m) {
        view.set(m);
    }

    public void setViewInv(Matrix4f m) {
        viewInv.set(m);
    }

    public void setProj(Matrix4f m) {
        proj.set(m);
    }

    public void setProjInv(Matrix4f m) {
        projInv.set(m);
    }

    public void setDLight(DirectionalLight light) {
        this.dLight = light;
    }
}
