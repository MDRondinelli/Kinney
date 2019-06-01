package me.marlon.util;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform {
    private Vector3f position;
    private Quaternionf orientation;
    private float scale;

    public Transform() {
        position = new Vector3f();
        orientation = new Quaternionf();
        scale = 1.0f;
    }

    public Transform translate(Vector3f v) {
        position.add(v);
        return this;
    }

    public Transform rotate(Quaternionf q) {
        orientation.mul(q);
        return this;
    }

    public Transform scale(float x) {
        scale *= x;
        return this;
    }

    public Matrix4f getMatrix() {
        return new Matrix4f().translationRotateScale(position, orientation, scale);
    }

    public Matrix4f getInvMatrix() {
        return new Matrix4f().translationRotateScaleInvert(position, orientation, scale);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f v) {
        position.set(v);
    }

    public Quaternionf getOrientation() {
        return orientation;
    }

    public void setOrientation(Quaternionf q) {
        orientation.set(q);
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float x) {
        scale = x;
    }
}
