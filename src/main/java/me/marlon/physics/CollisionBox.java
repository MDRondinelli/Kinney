package me.marlon.physics;

import org.joml.AABBf;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public class CollisionBox extends CollisionPrimitive {
    private Vector3f halfExtents;

    public CollisionBox(Matrix4f transform, Vector3f halfExtents) {
        super(transform, new AABBf(-halfExtents.x, -halfExtents.y, -halfExtents.z, halfExtents.x, halfExtents.y, halfExtents.z));
        this.halfExtents = halfExtents;
    }

    @Override
    protected void collideWith(CollisionSphere other, List<Contact> contacts) {
        CollisionDetector.collide(this, other, contacts);
    }

    @Override
    protected void collideWith(CollisionPlane other, List<Contact> contacts) {
        CollisionDetector.collide(this, other, contacts);
    }

    @Override
    protected void collideWith(CollisionBox other, List<Contact> contacts) {
        CollisionDetector.collide(this, other, contacts);
    }

    @Override
    protected void collideWith(CollisionTerrain other, List<Contact> contacts) {
        CollisionDetector.collide(this, other, contacts);
    }

    public Vector3f getHalfExtents() {
        return halfExtents;
    }

    public void setHalfExtents(Vector3f halfExtents) {
        this.halfExtents = halfExtents;
    }
}
