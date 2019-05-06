package me.marlon.game;

import me.marlon.util.Transform;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TransformComponent {
    public int parent;
    public Transform transform;

    public TransformComponent() {
        this(0xffffffff, new Transform());
    }

    public TransformComponent(int parent) {
        this(parent, new Transform());
    }

    public TransformComponent(Transform transform) {
        this(0xffffffff, transform);
    }

    public TransformComponent(int parent, Transform transform) {
        this.parent = parent;
        this.transform = transform;
    }

    public void translate(Vector3f v) {
        transform.translate(v);
    }

    public void rotate(Quaternionf q) {
        transform.rotate(q);
    }

    public void scale(float x) {
        transform.scale(x);
    }

    public Matrix4f getMatrix() {
        return transform.getMatrix();
    }

    public Matrix4f getInvMatrix() {
        return transform.getInvMatrix();
    }

    public Vector3f getPosition() {
        return transform.getPosition();
    }

    public void setPosition(Vector3f v) {
        transform.setPosition(v);
    }

    public Quaternionf getRotation() {
        return transform.getRotation();
    }

    public void setRotation(Quaternionf q) {
        transform.setRotation(q);
    }

    public float getScale() {
        return transform.getScale();
    }

    public void setScale(float x) {
        transform.setScale(x);
    }
}
