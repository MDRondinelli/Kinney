package me.marlon.physics;

import org.joml.Matrix4f;

import java.util.List;

public class CollisionPrimitive {
    private RigidBody body;
    private Matrix4f modelTransform;
    private Matrix4f worldTransform;
    private Matrix4f worldTransformInv;

    public CollisionPrimitive(RigidBody body, Matrix4f modelTransform) {
        this.body = body;
        this.modelTransform = modelTransform;
        this.worldTransform = new Matrix4f(modelTransform);
        this.worldTransformInv = new Matrix4f(worldTransform).invertAffine();
        updateDerivedData();
    }

    public void collideWith(CollisionSphere other, List<Contact> contacts) {
    }

    public void collideWith(CollisionPlane other, List<Contact> contacts) {
    }

    public void collideWith(CollisionBox other, List<Contact> contacts) {
    }

    public void updateDerivedData() {
        if (body != null) {
            worldTransform.set(body.getTransform()).mul(modelTransform);
            worldTransformInv.set(worldTransform).invertAffine();
        }
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

    public Matrix4f getWorldTransformInv() {
        return worldTransformInv;
    }
}
