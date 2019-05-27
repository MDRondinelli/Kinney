package me.marlon.physics;

import org.joml.AABBf;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public class CollisionBox extends CollisionPrimitive {
    private Vector3f halfExtents;

    public CollisionBox(RigidBody body, Matrix4f transform, Vector3f halfExtents) {
        super(body, transform, new AABBf(-halfExtents.x, -halfExtents.y, -halfExtents.z, halfExtents.x, halfExtents.y, halfExtents.z));
        this.halfExtents = halfExtents;
    }

    @Override
    protected void collideWith(CollisionSphere sphere, List<Contact> contacts) {
        CollisionDetector.collide(this, sphere, contacts);
    }

    @Override
    protected void collideWith(CollisionPlane plane, List<Contact> contacts) {
        CollisionDetector.collide(this, plane, contacts);
    }

    @Override
    protected void collideWith(CollisionBox box, List<Contact> contacts) {
        CollisionDetector.collide(this, box, contacts);
    }

    public Vector3f getHalfExtents() {
        return halfExtents;
    }

    public void setHalfExtents(Vector3f halfExtents) {
        this.halfExtents = halfExtents;
    }
}
