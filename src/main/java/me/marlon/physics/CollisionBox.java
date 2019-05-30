package me.marlon.physics;

import org.joml.AABBf;
import org.joml.Matrix4f;
import org.joml.Vector2f;
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

    @Override
    protected boolean collideWith(CollisionSphere other) {
        return CollisionDetector.collide(this, other);
    }

    @Override
    protected boolean collideWith(CollisionPlane other) {
        return CollisionDetector.collide(this, other);
    }

    @Override
    protected boolean collideWith(CollisionBox other) {
        return CollisionDetector.collide(this, other);
    }

    @Override
    protected boolean collideWith(CollisionTerrain other) {
        return CollisionDetector.collide(this, other);
    }

    @Override
    public Float rayCast(Vector3f o, Vector3f d) {
        Vector3f ro = new Vector3f(o).mulPosition(getWorldTransformInv());
        Vector3f rd = new Vector3f(d).mulDirection(getWorldTransformInv());

        AABBf aabb = new AABBf();
        aabb.setMin(-halfExtents.x, -halfExtents.y, -halfExtents.z);
        aabb.setMax(halfExtents.x, halfExtents.y, halfExtents.z);

        Vector2f ts = new Vector2f();
        if (aabb.intersectRay(ro.x, ro.y, ro.z, rd.x, rd.y, rd.z, ts)) {
            if (ts.x > 0.0f)
                return ts.x;
            if (ts.y > 0.0f)
                return ts.y;
        }

        return null;
    }

    public Vector3f getHalfExtents() {
        return halfExtents;
    }

    public void setHalfExtents(Vector3f halfExtents) {
        this.halfExtents = halfExtents;
    }
}
