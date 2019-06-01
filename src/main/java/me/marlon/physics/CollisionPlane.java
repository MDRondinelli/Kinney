package me.marlon.physics;

import org.joml.AABBf;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public class CollisionPlane extends Collider {
    private Vector3f normal;
    private float offset;

    public CollisionPlane(PhysicsMaterial material, Vector3f normal, float offset) {
        super(material, new Matrix4f(), new AABBf(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE));
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

    @Override
    protected boolean collideWith(CollisionSphere other) {
        return CollisionDetector.collide(this, other);
    }

    @Override
    protected boolean collideWith(CollisionBox other) {
        return CollisionDetector.collide(this, other);
    }

    @Override
    public Float rayCast(Vector3f o, Vector3f d) {
        return -(o.dot(normal) + offset) / d.dot(normal);
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
