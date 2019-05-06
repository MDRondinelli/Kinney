package me.marlon.gfx;

import org.joml.Matrix4f;

public class MeshInstance {
    public Mesh mesh;
    public Matrix4f matrix;

    public MeshInstance(Mesh mesh, Matrix4f matrix) {
        this.mesh = mesh;
        this.matrix = matrix;
    }
}
