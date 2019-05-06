package me.marlon.util;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform {
    private Vector3f position;
    private Quaternionf rotation;
    private float scale;

    public Transform() {
        position = new Vector3f();
        rotation = new Quaternionf();
        scale = 1.0f;
    }

    public void translate(Vector3f v) {
        position.add(v);
    }

    public void rotate(Quaternionf q) {
        rotation.mul(q);
    }

    public void scale(float x) {
        scale *= x;
    }

    public Matrix4f getMatrix() {
        return new Matrix4f().translationRotateScale(position, rotation, scale);
    }

    public Matrix4f getInvMatrix() {
        return new Matrix4f().translationRotateScaleInvert(position, rotation, scale);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f v) {
        position.set(v);
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public void setRotation(Quaternionf q) {
        rotation.set(q);
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float x) {
        scale = x;
    }
}
