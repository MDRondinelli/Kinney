package me.marlon.gfx;

import me.marlon.ecs.Terrain;
import org.joml.AABBf;
import org.joml.FrustumIntersection;
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
import java.util.List;

public class Renderer implements AutoCloseable {
    private Framebuffer gbuffer;
    private Framebuffer pbuffer;
    private Primitive screenMesh;

    private UniformBuffer cameraBlock;
    private ByteBuffer cameraData;

    private UniformBuffer lightBlock;
    private ByteBuffer lightData;

    private Shader meshShader;
    private Shader terrainShader;
    private Shader waterShader;

    private Shader lightShader;
    private Shader postProcessShader;

    private List<MeshInstance> queue;
    private TerrainMesh terrainMesh;
    private Matrix4f terrainTransform;
    private WaterMesh waterMesh;
    private Matrix4f waterTransform;
    private Matrix4f view;
    private Matrix4f viewInv;
    private Matrix4f proj;
    private Matrix4f projInv;
    private DirectionalLight dLight;

    public Renderer(int width, int height) {
        gbuffer = new Framebuffer(width, height, new int[] { GL_RGBA8, GL_RGBA8 });
        pbuffer = new Framebuffer(width, height, new int[] { GL_RGBA16F });

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

            screenMesh = new Primitive(vertices.rewind(), null);
        }

        cameraBlock = new UniformBuffer(256);
        cameraData = memAlloc(cameraBlock.getSize());

        lightBlock = new UniformBuffer(32);
        lightData = memAlloc(lightBlock.getSize());

        meshShader = new Shader();
        terrainShader = new Shader();
        waterShader = new Shader();
        lightShader = new Shader();
        postProcessShader = new Shader();

        try {
            meshShader.setVertText(Files.readString(Paths.get("res/shaders/mesh.vert")));
            meshShader.setFragText(Files.readString(Paths.get("res/shaders/mesh.frag")));

            terrainShader.setVertText(Files.readString(Paths.get("res/shaders/terrain.vert")));
            terrainShader.setFragText(Files.readString(Paths.get("res/shaders/terrain.frag")));

            waterShader.setVertText(Files.readString(Paths.get("res/shaders/water.vert")));
            waterShader.setFragText(Files.readString(Paths.get("res/shaders/water.frag")));

            lightShader.setVertText(Files.readString(Paths.get("res/shaders/light.vert")));
            lightShader.setFragText(Files.readString(Paths.get("res/shaders/light.frag")));

            postProcessShader.setVertText(Files.readString(Paths.get("res/shaders/postprocess.vert")));
            postProcessShader.setFragText(Files.readString(Paths.get("res/shaders/postprocess.frag")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        meshShader.compile();
        terrainShader.compile();
        waterShader.compile();
        lightShader.compile();
        postProcessShader.compile();

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

        cameraBlock.close();
        memFree(cameraData);

        lightBlock.close();
        memFree(lightData);

        meshShader.close();
        terrainShader.close();
        waterShader.close();
        lightShader.close();
        postProcessShader.close();
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
        view.get(0, cameraData);
        viewInv.get(64, cameraData);
        proj.get(128, cameraData);
        projInv.get(192, cameraData);
        cameraBlock.buffer(cameraData);
        cameraBlock.bind(0);

        if (dLight == null) {
            new Vector4f(0.0f).get(0, lightData);
            new Vector4f(0.0f).get(16, lightData);
        } else {
            dLight.color.get(0, lightData);
            dLight.direction.get(16, lightData);
        }

        lightBlock.buffer(lightData);
        lightBlock.bind(1);

        terrainShader.set("model", terrainTransform);
        waterShader.set("model", waterTransform);

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
            for (int j = 0; j < primitives.length; ++j) {
                meshShader.set("albedo", primitives[j].getAlbedo());
                primitives[j].draw();
            }
        }

        if (terrainMesh != null) {
            terrainShader.bind();

            Matrix4f matrix = new Matrix4f().mul(proj).mul(view).mul(terrainTransform);
            FrustumIntersection frustum = new FrustumIntersection(matrix);

            List<TerrainChunk> chunks = terrainMesh.getChunks();

            for (int i = 0; i < chunks.size(); ++i) {
                TerrainChunk chunk = chunks.get(i);
                AABBf bounds = chunk.getBounds();

                if (frustum.testAab(bounds.minX, bounds.minY, bounds.minZ, bounds.maxX, bounds.maxY, bounds.maxZ))
                    chunk.draw();
            }
        }

        pbuffer.bind(GL_FRAMEBUFFER);
        glDisable(GL_DEPTH_TEST);

        lightShader.bind();
        gbuffer.bindTexture(0, 0);
        gbuffer.bindTexture(1, 1);
        gbuffer.bindTexture(2, 2);
        screenMesh.draw();

        if (waterMesh != null) {
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_DEPTH_TEST);

            glBlitNamedFramebuffer(gbuffer.getHandle(), pbuffer.getHandle(),
                    0, 0, gbuffer.getWidth(), gbuffer.getHeight(),
                    0, 0, pbuffer.getWidth(), pbuffer.getHeight(),
                    GL_DEPTH_BUFFER_BIT, GL_NEAREST);

            waterShader.bind();
            waterMesh.draw();

            glDisable(GL_BLEND);
            glDisable(GL_DEPTH_TEST);
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        postProcessShader.bind();
        pbuffer.bindTexture(0, 0);
        pbuffer.bindTexture(1, 1);
        screenMesh.draw();
    }

    public void setTerrainMesh(TerrainMesh mesh, Matrix4f transform) {
        terrainMesh = mesh;
        terrainTransform = transform;
    }

    public void setWaterMesh(WaterMesh mesh, Matrix4f transform) {
        waterMesh = mesh;
        waterTransform = transform;
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
