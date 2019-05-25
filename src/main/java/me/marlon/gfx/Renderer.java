package me.marlon.gfx;

import org.joml.*;
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

        lightBlock = new UniformBuffer(304);
        lightData = memAlloc(lightBlock.getSize());

        meshShader = new Shader();
        terrainShader = new Shader();
        waterShader = new Shader();
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
        lightShader.compile();
        shadowShader.compile();
        postProcessShader.compile();

        queue = new ArrayList<>();
        view = new Matrix4f();
        viewInv = new Matrix4f();
        proj = new Matrix4f();
        projInv = new Matrix4f();

        dLightShadows = new ShadowCascade(new int[] { 2048, 2048, 2048, 2048 });

        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_CLAMP);
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
        shadowShader.set("model", new Matrix4f());

        if (terrainMesh != null && dLight != null) {
            List<TerrainChunk> chunks = terrainMesh.getChunks();

            for (int i = 0; i < dLightShadows.getNumCascades(); ++i) {
                ShadowMap shadowMap = dLightShadows.getShadowMap(i);
                Matrix4f matrix = dLightShadows.getMatrix(i);
                AABBf bounds = dLightShadows.getBounds(i);

                shadowMap.bindFramebuffer(GL_FRAMEBUFFER);
                glViewport(0, 0, shadowMap.getSize(), shadowMap.getSize());
                glClear(GL_DEPTH_BUFFER_BIT);

                shadowShader.set("slice", i);

                Vector3f min = new Vector3f();
                Vector3f max = new Vector3f();
                for (int j = 0; j < chunks.size(); ++j) {
                    TerrainChunk chunk = chunks.get(j);

                    min.set(chunk.getBounds().minX, chunk.getBounds().minY, chunk.getBounds().minZ);
                    max.set(chunk.getBounds().maxX, chunk.getBounds().maxY, chunk.getBounds().maxZ);

                    matrix.transformAab(min, max, min, max);

                    if (bounds.maxX >= min.x && bounds.minX <= max.x && bounds.maxY >= min.y && bounds.minY <= max.y);
                        chunks.get(j).draw();
                }
            }
        }

        glDisable(GL_POLYGON_OFFSET_FILL);
    }

    public void submitDraw() {
        glViewport(0, 0, gbuffer.getWidth(), gbuffer.getHeight());

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

            Matrix4f matrix = new Matrix4f().mul(proj).mul(view);
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

        for (int i = 0; i < dLightShadows.getNumCascades(); ++i)
            dLightShadows.getShadowMap(i).bindTexture(i + 3);

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
