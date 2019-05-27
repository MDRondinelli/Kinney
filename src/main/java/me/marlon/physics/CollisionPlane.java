package me.marlon.physics;

import org.joml.AABBf;
import org.joml.Vector3f;

import java.util.List;

public class CollisionPlane extends CollisionPrimitive {
    private Vector3f normal;
    private float offset;

    public CollisionPlane(Vector3f normal, float offset) {
        super(null, null, new AABBf(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE));
        this.normal = normal;
        this.offset = offset;
    }

    @Override
    protected void collideWith(CollisionSphere other, List<Contact> contacts) {
        CollisionDetector.collide(this, other, contacts);
    }

    @Override
    protected void collideWith(CollisionBox other, List<Contact> contacts) {
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
