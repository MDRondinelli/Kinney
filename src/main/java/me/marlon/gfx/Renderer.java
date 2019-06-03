package me.marlon.gfx;

import me.marlon.gui.GuiManager;
import org.joml.*;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.io.IOException;
import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Renderer implements AutoCloseable {
    private GuiManager gui;
    private Framebuffer guiBuffer;
    private Framebuffer gbuffer;
    private Framebuffer pbuffer;
    private Primitive screenMesh;

    private UniformBuffer cameraBlock;
    private ByteBuffer cameraData;

    private UniformBuffer lightBlock;
    private ByteBuffer lightData;

    private UniformBuffer ssaoBlock;
    private Texture ssaoNoise;
    private Texture ssaoTexture0;
    private Texture ssaoTexture1;

    private Shader meshShader;
    private Shader terrainShader;
    private Shader waterShader;

    private Shader ssaoShader;
    private Shader blurShader;
    private Shader lightShader;
    private Shader shadowShader;
    private Shader postProcessShader;

    private List<MeshInstance> queue;
    private TerrainMesh terrainMesh;
    private WaterMesh waterMesh;
    private Matrix4f waterTransform;
    private Matrix4f view;
    private Matrix4f viewInv;
    private Matrix4f proj;
    private Matrix4f projInv;
    private DirectionalLight dLight;
    private ShadowCascade dLightShadows;

    public Renderer(GuiManager gui) {
        this.gui = gui;

        guiBuffer = new Framebuffer(gui.getWidth(), gui.getHeight(), new int[] { GL_RGBA8 });
        gbuffer = new Framebuffer(gui.getWidth(), gui.getHeight(), new int[] { GL_RGBA8, GL_RGBA8 });
        pbuffer = new Framebuffer(gui.getWidth(), gui.getHeight(), new int[] { GL_RGBA16F });

        try (MemoryStack stack = stackPush()) {
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

        try (MemoryStack stack = stackPush()) {
            ByteBuffer samples = stack.malloc(16 * 64);

            for (int i = 0; i < 64; ++i) {
                float r = (float) Math.sqrt(Math.random());
                float theta = (float) Math.random() * (float) Math.PI * 2.0f;

                float x = r * (float) Math.cos(theta);
                float y = r * (float) Math.sin(theta);
                float z = (float) Math.sqrt(1.0f - x * x - y * y);

                Vector3f sample = new Vector3f(x, y, z);
                float scale = (float) Math.random();
                scale *= scale;
                scale += 0.1f * (1.0f - scale);
                sample.mul(scale);

                samples.putFloat(i * 16, sample.x);
                samples.putFloat(i * 16 + 4, sample.y);
                samples.putFloat(i * 16 + 8, sample.z);
            }

            ssaoBlock = new UniformBuffer(16 * 64);
            ssaoBlock.buffer(samples.rewind());
            ssaoBlock.bind(2);
        }

        {
            FloatBuffer noise = memAllocFloat(128 * 128 * 2);

            for (int i = 0; i < 128 * 128;) {
                float x = (float) Math.random() * 2.0f - 1.0f;
                float y = (float) Math.random() * 2.0f - 1.0f;

                if (x * x + y * y < 1.0f) {
                    noise.put(i * 2, x);
                    noise.put(i * 2 + 1, y);
                    ++i;
                }
            }

            ssaoNoise = new Texture(128, 128, GL_RG16F);
            ssaoNoise.image(GL_RG, noise.rewind());

            memFree(noise);
        }

        ssaoTexture0 = new Texture(gui.getWidth(), gui.getHeight(), GL_R8);
        ssaoTexture1 = new Texture(gui.getWidth(), gui.getHeight(), GL_R8);

        cameraBlock = new UniformBuffer(256);
        cameraData = memAlloc(cameraBlock.getSize());

        lightBlock = new UniformBuffer(304);
        lightData = memAlloc(lightBlock.getSize());

        meshShader = new Shader();
        terrainShader = new Shader();
        waterShader = new Shader();
        ssaoShader = new Shader();
        blurShader = new Shader();
        lightShader = new Shader();
        shadowShader = new Shader();
        postProcessShader = new Shader();

        try {
            meshShader.setVertText(Files.readString(Paths.get("res/shaders/mesh.vert")));
            meshShader.setFragText(Files.readString(Paths.get("res/shaders/mesh.frag")));

            terrainShader.setVertText(Files.readString(Paths.get("res/shaders/terrain.vert")));
            terrainShader.setFragText(Files.readString(Paths.get("res/shaders/terrain.frag")));

            waterShader.setVertText(Files.readString(Paths.get("res/shaders/water.vert")));
            waterShader.setFragText(Files.readString(Paths.get("res/shaders/water.frag")));

            ssaoShader.setCompText(Files.readString(Paths.get("res/shaders/ssao.comp")));

            blurShader.setCompText(Files.readString(Paths.get("res/shaders/blur.comp")));

            lightShader.setVertText(Files.readString(Paths.get("res/shaders/light.vert")));
            lightShader.setFragText(Files.readString(Paths.get("res/shaders/light.frag")));

            shadowShader.setVertText(Files.readString(Paths.get("res/shaders/shadow.vert")));
            shadowShader.setFragText(Files.readString(Paths.get("res/shaders/shadow.frag")));

            postProcessShader.setVertText(Files.readString(Paths.get("res/shaders/postprocess.vert")));
            postProcessShader.setFragText(Files.readString(Paths.get("res/shaders/postprocess.frag")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        meshShader.compile();
        terrainShader.compile();
        waterShader.compile();
        ssaoShader.compile();
        blurShader.compile();
        lightShader.compile();
        shadowShader.compile();
        postProcessShader.compile();

        queue = new ArrayList<>();
        view = new Matrix4f();
        viewInv = new Matrix4f();
        proj = new Matrix4f();
        projInv = new Matrix4f();

        dLightShadows = new ShadowCascade(new int[] { 4096, 4096, 4096, 4096 });

        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_CLAMP);
        glEnable(GL_FRAMEBUFFER_SRGB);
    }

    public void close() {
        gbuffer.close();
        screenMesh.close();

        cameraBlock.close();
        memFree(cameraData);

        lightBlock.close();
        memFree(lightData);

        ssaoBlock.close();
        ssaoNoise.close();
        ssaoTexture0.close();
        ssaoTexture1.close();

        meshShader.close();
        terrainShader.close();
        waterShader.close();
        lightShader.close();
        postProcessShader.close();

        dLightShadows.close();
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
            new Vector4f(0.0f).get(272, lightData);
            new Vector4f(0.0f).get(288, lightData);
        } else {
            dLightShadows.update(viewInv, projInv, dLight.direction);

            for (int i = 0; i < dLightShadows.getNumCascades(); ++i)
                dLightShadows.getMatrix(i).get(i * 64, lightData);

            for (int i = 0; i < dLightShadows.getNumCascades(); ++i)
                lightData.putFloat(i * 4 + 256, dLightShadows.getFarPlane(i));

            dLight.color.get(272, lightData);
            dLight.direction.get(288, lightData);
        }

        lightBlock.buffer(lightData);
        lightBlock.bind(1);

        waterShader.set("model", waterTransform);
        waterShader.set("time", (float) glfwGetTime());

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_POLYGON_OFFSET_FILL);
        glPolygonOffset(2, 1);

        shadowShader.bind();

        if (terrainMesh != null && dLight != null) {
            List<TerrainChunk> chunks = terrainMesh.getChunks();

            for (int i = 0; i < dLightShadows.getNumCascades(); ++i) {
                ShadowMap shadowMap = dLightShadows.getShadowMap(i);

                shadowMap.bindFramebuffer(GL_FRAMEBUFFER);
                glViewport(0, 0, shadowMap.getSize(), shadowMap.getSize());
                glClear(GL_DEPTH_BUFFER_BIT);

                shadowShader.set("slice", i);
                shadowShader.set("model", new Matrix4f());

                for (TerrainChunk chunk : chunks)
                    chunk.draw();

                for (MeshInstance mesh : queue) {
                    shadowShader.set("model", mesh.matrix);

                    for (Primitive primitive : mesh.mesh.getPrimitives())
                        primitive.draw();
                }
            }
        }

        glDisable(GL_POLYGON_OFFSET_FILL);
    }

    private void drawMeshes() {
        meshShader.bind();

        for (MeshInstance mesh : queue) {
            meshShader.set("model", mesh.matrix);

            for (Primitive primitive : mesh.mesh.getPrimitives()) {
                meshShader.set("albedo", primitive.getAlbedo());
                primitive.draw();
            }
        }
    }

    private void drawTerrain() {
        if (terrainMesh != null) {
            terrainShader.bind();

            Matrix4f matrix = new Matrix4f().mul(proj).mul(view);
            FrustumIntersection frustum = new FrustumIntersection(matrix);

            for (TerrainChunk chunk : terrainMesh.getChunks()) {
                AABBf bounds = chunk.getBounds();

                if (frustum.testAab(bounds.minX, bounds.minY, bounds.minZ, bounds.maxX, bounds.maxY, bounds.maxZ))
                    chunk.draw();
            }
        }
    }

    private void drawSsao() {
        ssaoShader.bind();
        ssaoTexture0.bindImage(0, GL_WRITE_ONLY, GL_R8);
        gbuffer.bindTexture(0, 0);
        gbuffer.bindTexture(1, 1);
        ssaoNoise.bind(2);

        glMemoryBarrier(GL_TEXTURE_FETCH_BARRIER_BIT);
        glDispatchCompute((ssaoTexture0.getWidth() + 15) / 16, (ssaoTexture0.getHeight() + 15) / 16, 1);

        blurShader.bind();

        blurShader.set("direction", new Vector2i(1, 0));
        ssaoTexture0.bindImage(0, GL_READ_ONLY, GL_R8);
        ssaoTexture1.bindImage(1, GL_WRITE_ONLY, GL_R8);
        glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
        glDispatchCompute((ssaoTexture0.getWidth() + 15) / 16, (ssaoTexture0.getHeight() + 15) / 16, 1);

        blurShader.set("direction", new Vector2i(0, 1));
        ssaoTexture1.bindImage(0, GL_READ_ONLY, GL_R8);
        ssaoTexture0.bindImage(1, GL_WRITE_ONLY, GL_R8);
        glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
        glDispatchCompute((ssaoTexture0.getWidth() + 15) / 16, (ssaoTexture0.getHeight() + 15) / 16, 1);
    }

    private void drawLight() {
        lightShader.bind();
        gbuffer.bindTexture(0, 0);
        gbuffer.bindTexture(1, 1);
        gbuffer.bindTexture(2, 2);
        ssaoTexture0.bind(3);

        for (int i = 0; i < dLightShadows.getNumCascades(); ++i)
            dLightShadows.getShadowMap(i).bindTexture(i + 4);

        glMemoryBarrier(GL_TEXTURE_FETCH_BARRIER_BIT);
        screenMesh.draw();
    }

    private void drawWater() {
        if (waterMesh != null) {
            cameraBlock.bind(0);

            waterShader.bind();
            waterMesh.draw();
        }
    }

    private void drawGui() {
        gui.draw();
    }

    public void submitDraw() {
        cameraBlock.bind(0);
        lightBlock.bind(1);
        ssaoBlock.bind(2);

        gbuffer.bind(GL_FRAMEBUFFER);
        glViewport(0, 0, gbuffer.getWidth(), gbuffer.getHeight());
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);

        drawMeshes();
        drawTerrain();
        drawSsao();

        pbuffer.bind(GL_FRAMEBUFFER);
        glViewport(0, 0, pbuffer.getWidth(), pbuffer.getHeight());
        glDisable(GL_DEPTH_TEST);

        drawLight();

        glBlitNamedFramebuffer(gbuffer.getHandle(), pbuffer.getHandle(),
                0, 0, gbuffer.getWidth(), gbuffer.getHeight(),
                0, 0, pbuffer.getWidth(), pbuffer.getHeight(),
                GL_DEPTH_BUFFER_BIT, GL_NEAREST);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_DEPTH_TEST);

        drawWater();

        guiBuffer.bind(GL_FRAMEBUFFER);
        glViewport(0, 0, guiBuffer.getWidth(), guiBuffer.getHeight());
        glClear(GL_COLOR_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);

        drawGui();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glDisable(GL_BLEND);
        glDisable(GL_DEPTH_TEST);

        postProcessShader.bind();
        pbuffer.bindTexture(0, 0);
        pbuffer.bindTexture(1, 1);
        guiBuffer.bindTexture(1, 2);
        screenMesh.draw();
    }

    public void setTerrainMesh(TerrainMesh mesh) {
        terrainMesh = mesh;
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
