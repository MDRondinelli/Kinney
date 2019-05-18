package me.marlon.physics;

import org.joml.Vector3f;

import java.util.List;

public class CollisionPlane extends CollisionPrimitive {
    private Vector3f normal;
    private float offset;

    public CollisionPlane(Vector3f normal, float offset) {
        super(null, null);
        this.normal = normal;
        this.offset = offset;
    }

    @Override
    public void collideWith(CollisionSphere other, List<Contact> contacts) {
        CollisionDetector.collide(this, other, contacts);
    }

    @Override
    public void collideWith(CollisionBox other, List<Contact> contacts) {
        CollisionDetector.collide(this, other, contacts);
    }

    public Vector3f getNormal() {
        return normal;
    }

    public void setNormal(Vector3f normal) {
        this.normal = normal;
    }

    public float getOffset() {
        return offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }
}
