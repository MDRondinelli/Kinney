package me.marlon.ecs;

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

    public TransformComponent translate(Vector3f v) {
        transform.translate(v);
        return this;
    }

    public TransformComponent rotate(Quaternionf q) {
        transform.rotate(q);
        return this;
    }

    public TransformComponent scale(float x) {
        transform.scale(x);
        return this;
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

    public Quaternionf getOrientation() {
        return transform.getOrientation();
    }

    public void setOrientation(Quaternionf q) {
        transform.setOrientation(q);
    }

    public float getScale() {
        return transform.getScale();
    }

    public void setScale(float x) {
        transform.setScale(x);
    }
}
