package me.marlon.physics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public class CollisionBox extends CollisionPrimitive {
    private Vector3f halfExtents;

    public CollisionBox(Vector3f halfExtents, RigidBody body, Matrix4f transform) {
        super(body, transform);
        this.halfExtents = halfExtents;
    }

    @Override
    public void collideWith(CollisionSphere sphere, List<Contact> contacts) {
        CollisionDetector.collide(this, sphere, contacts);
    }

    @Override
    public void collideWith(CollisionPlane plane, List<Contact> contacts) {
        CollisionDetector.collide(this, plane, contacts);
    }

    public Vector3f getHalfExtents() {
        return halfExtents;
    }

    public void setHalfExtents(Vector3f halfExtents) {
        this.halfExtents = halfExtents;
    }
}
