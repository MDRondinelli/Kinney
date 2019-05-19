package me.marlon.gfx;

import org.joml.AABBf;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class ShadowCascade implements AutoCloseable {
    private static final float LAMBDA = 0.65f;
    private static final Vector4f[] CORNERS = {
            new Vector4f(-1.0f, -1.0f, -1.0f, 1.0f),
            new Vector4f(1.0f, -1.0f, -1.0f, 1.0f),
            new Vector4f(-1.0f, 1.0f, -1.0f, 1.0f),
            new Vector4f(1.0f, 1.0f, -1.0f, 1.0f),

            new Vector4f(-1.0f, -1.0f, 1.0f, 1.0f),
            new Vector4f(1.0f, -1.0f, 1.0f, 1.0f),
            new Vector4f(-1.0f, 1.0f, 1.0f, 1.0f),
            new Vector4f(1.0f, 1.0f, 1.0f, 1.0f)
    };

    private ShadowMap[] shadowMaps;
    private Matrix4f[] matrices;
    private float[] depths;

    public ShadowCascade(int[] resolutions) {
        shadowMaps = new ShadowMap[resolutions.length];

        for (int i = 0; i < resolutions.length; ++i)
            shadowMaps[i] = new ShadowMap(resolutions[i]);

        matrices = new Matrix4f[resolutions.length];

        for (int i = 0; i < resolutions.length; ++i)
            matrices[i] = new Matrix4f();

        depths = new float[resolutions.length + 1];
    }

    @Override
    public void close() {
        for (int i = 0; i < shadowMaps.length; ++i)
            shadowMaps[i].close();
    }

    public void update(Matrix4f viewInv, Matrix4f projInv, Vector3f lightDirection) {
        int numCascades = getNumCascades();

        Vector4f[] corners = new Vector4f[8];
        for (int i = 0; i < 8; ++i) {
            corners[i] = projInv.transform(CORNERS[i], new Vector4f());
            corners[i].div(corners[i].w);
        }

        float n = -corners[0].z;
        float f = -corners[4].z;

        for (int i = 0; i <= numCascades; ++i) {
            float cLog = n * (float) Math.pow(f / n, (float) i / numCascades);
            float cLin = n + (f - n) * i / numCascades;
            depths[i] = LAMBDA * cLog + (1.0f - LAMBDA) * cLin;
        }

        Vector3f vec0 = new Vector3f(corners[4].x - corners[0].x, corners[4].y - corners[0].y, corners[4].z - corners[0].z);
        vec0.div(-vec0.z);
        Vector3f vec1 = new Vector3f(corners[5].x - corners[1].x, corners[5].y - corners[1].y, corners[5].z - corners[1].z);
        vec1.div(-vec1.z);
        Vector3f vec2 = new Vector3f(corners[6].x - corners[2].x, corners[6].y - corners[2].y, corners[6].z - corners[2].z);
        vec2.div(-vec2.z);
        Vector3f vec3 = new Vector3f(corners[7].x - corners[3].x, corners[7].y - corners[3].y, corners[7].z - corners[3].z);
        vec3.div(-vec3.z);

        Matrix4f lightView = new Matrix4f().lookAlong(lightDirection, new Vector3f(0.0f, 1.0f, 0.0f));

        for (int i = 0; i < 4; ++i)
            lightView.transform(viewInv.transform(corners[i]));

        lightView.transformDirection(viewInv.transformDirection(vec0));
        lightView.transformDirection(viewInv.transformDirection(vec1));
        lightView.transformDirection(viewInv.transformDirection(vec2));
        lightView.transformDirection(viewInv.transformDirection(vec3));

        for (int i = 0; i < numCascades; ++i) {
            AABBf bounds = new AABBf();
            bounds.union(new Vector3f(vec0).mul(depths[i]).add(corners[0].x, corners[0].y, corners[0].z));
            bounds.union(new Vector3f(vec1).mul(depths[i]).add(corners[1].x, corners[1].y, corners[1].z));
            bounds.union(new Vector3f(vec2).mul(depths[i]).add(corners[2].x, corners[2].y, corners[2].z));
            bounds.union(new Vector3f(vec3).mul(depths[i]).add(corners[3].x, corners[3].y, corners[3].z));

            bounds.union(new Vector3f(vec0).mul(depths[i + 1]).add(corners[0].x, corners[0].y, corners[0].z));
            bounds.union(new Vector3f(vec1).mul(depths[i + 1]).add(corners[1].x, corners[1].y, corners[1].z));
            bounds.union(new Vector3f(vec2).mul(depths[i + 1]).add(corners[2].x, corners[2].y, corners[2].z));
            bounds.union(new Vector3f(vec3).mul(depths[i + 1]).add(corners[3].x, corners[3].y, corners[3].z));

            Matrix4f lightProj = new Matrix4f().ortho(bounds.minX, bounds.maxX, bounds.minY, bounds.maxY, -bounds.maxZ, -bounds.minZ);
            matrices[i].identity().mul(lightProj).mul(lightView);
        }
    }

//    public int getSize() {
//        return shadowMaps[0].getSize();
//    }

    public int getNumCascades() {
        return shadowMaps.length;
    }

    public ShadowMap getShadowMap(int cascade) {
        return shadowMaps[cascade];
    }

    public Matrix4f getMatrix(int cascade) {
        return matrices[cascade];
    }

    public float getFarPlane(int cascade) {
        return depths[cascade + 1];
    }
}
