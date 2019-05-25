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
        this.modelTransform = modelTransform == null ? new Matrix4f() : new Matrix4f(modelTransform);
        this.worldTransform = new Matrix4f(this.modelTransform);
        this.worldTransformInv = new Matrix4f(this.worldTransform).invertAffine();
        updateDerivedData();
    }

    protected void collideWith(CollisionSphere other, List<Contact> contacts) {
    }

    protected void collideWith(CollisionPlane other, List<Contact> contacts) {
    }

    protected void collideWith(CollisionBox other, List<Contact> contacts) {
    }

    public void collideWith(CollisionPrimitive other, List<Contact> contacts) {
        if (other instanceof CollisionSphere)
            collideWith((CollisionSphere) other, contacts);
        else if (other instanceof CollisionPlane)
            collideWith((CollisionPlane) other, contacts);
        else if (other instanceof CollisionBox)
            collideWith((CollisionBox) other, contacts);
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
