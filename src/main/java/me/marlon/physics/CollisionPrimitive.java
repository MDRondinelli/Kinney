package me.marlon.physics;

import org.joml.Matrix4f;

import java.util.List;

public class CollisionPrimitive {
    private RigidBody body;
    private Matrix4f modelTransform;
    private Matrix4f worldTransform;

    public CollisionPrimitive(RigidBody body, Matrix4f modelTransform) {
        this.body = body;
        this.modelTransform = modelTransform;
        this.worldTransform = new Matrix4f();
        updateDerivedData();
    }

    public void collideWith(CollisionSphere other, List<Contact> contacts) {
    }

    public void collideWith(CollisionPlane other, List<Contact> contacts) {
    }

    public void collideWith(CollisionBox other, List<Contact> contacts) {
    }

    public void updateDerivedData() {
        if (body != null)
            worldTransform.set(body.getTransform()).mul(modelTransform);
    }

    public RigidBody getBody() {
        return body;
    }

    public void setBody(RigidBody body) {
        this.body = body;
    }

    public Matrix4f getModelTransform() {
        return modelTransform;
    }

    public Matrix4f getWorldTransform() {
        return worldTransform;
    }
}
