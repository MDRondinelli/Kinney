package me.marlon.physics;

import org.joml.AABBf;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public class CollisionPrimitive {
    private RigidBody body;
    private Matrix4f transform;
    private Matrix4f worldTransform;
    private Matrix4f worldTransformInv;
    private AABBf aabb;
    private AABBf worldAabb;

    public CollisionPrimitive(Matrix4f transform, AABBf aabb) {
        this.transform = transform == null ? new Matrix4f() : new Matrix4f(transform);
        this.aabb = aabb;

        worldTransform = new Matrix4f(this.transform);
        worldTransformInv = new Matrix4f(worldTransform).invertAffine();

        worldAabb = new AABBf(this.aabb);
        Vector3f min = new Vector3f(worldAabb.minX, worldAabb.minY, worldAabb.minZ);
        Vector3f max = new Vector3f(worldAabb.maxX, worldAabb.maxY, worldAabb.maxZ);
        worldTransform.transformAab(min, max, min, max);
        worldAabb.setMin(min);
        worldAabb.setMax(max);
        updateDerivedData();
    }

    protected void collideWith(CollisionSphere other, List<Contact> contacts) {
    }

    protected void collideWith(CollisionPlane other, List<Contact> contacts) {
    }

    protected void collideWith(CollisionBox other, List<Contact> contacts) {
    }

    protected void collideWith(CollisionTerrain other, List<Contact> contacts) {
    }

    public void collideWith(CollisionPrimitive other, List<Contact> contacts) { // replace with other.collideWIth(this) in each child. That way, the type of this is known
        if (other instanceof CollisionSphere)
            collideWith((CollisionSphere) other, contacts);
        else if (other instanceof CollisionPlane)
            collideWith((CollisionPlane) other, contacts);
        else if (other instanceof CollisionBox)
            collideWith((CollisionBox) other, contacts);
        else if (other instanceof CollisionTerrain)
            collideWith((CollisionTerrain) other, contacts);
    }

    public void updateDerivedData() {
        if (body != null) {
            worldTransform.set(body.getTransform()).mul(transform);
            worldTransformInv.set(worldTransform).invertAffine();

            Vector3f min = new Vector3f(aabb.minX, aabb.minY, aabb.minZ);
            Vector3f max = new Vector3f(aabb.maxX, aabb.maxY, aabb.maxZ);
            worldTransform.transformAab(min, max, min, max);
            worldAabb.setMin(min);
            worldAabb.setMax(max);
        }
    }

    public RigidBody getBody() {
        return body;
    }

    public void setBody(RigidBody body) {
        this.body = body;
    }

    public Matrix4f getWorldTransform() {
        return worldTransform;
    }

    public Matrix4f getWorldTransformInv() {
        return worldTransformInv;
    }

    public AABBf getWorldAabb() {
        return worldAabb;
    }
}
