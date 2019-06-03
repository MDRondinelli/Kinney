package me.marlon.physics;

import org.joml.AABBf;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;

public class CollisionBox extends Collider {
    private Vector3f halfExtents;

    public CollisionBox(PhysicsMaterial material, Matrix4f transform, Vector3f halfExtents) {
        super(material, transform, new AABBf(-halfExtents.x, -halfExtents.y, -halfExtents.z, halfExtents.x, halfExtents.y, halfExtents.z));
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
    public Intersection rayCast(Vector3f o, Vector3f d) {
        Vector3f ro = new Vector3f(o).mulPosition(getWorldTransformInv());
        Vector3f rd = new Vector3f(d).mulDirection(getWorldTransformInv());

        AABBf aabb = new AABBf();
        aabb.setMin(-halfExtents.x, -halfExtents.y, -halfExtents.z);
        aabb.setMax(halfExtents.x, halfExtents.y, halfExtents.z);

        Vector2f ts = new Vector2f();
        if (aabb.intersectRay(ro.x, ro.y, ro.z, rd.x, rd.y, rd.z, ts)) {
            float t;
            if (ts.x > 0.0f)
                t = ts.x;
            else if (ts.y > 0.0f)
                t = ts.y;
            else
                return null;

            Vector3f position = new Vector3f(ro).add(rd.x * t, rd.y * t, rd.z * t);
            Vector3f unit = new Vector3f(position).absolute().div(halfExtents);
            Vector3f normal = new Vector3f();

            if (unit.x > unit.y) {
                if (unit.x > unit.z)
                    normal.x = 1.0f;
                else
                    normal.z = 1.0f;
            } else {
                if (unit.y > unit.z)
                    normal.y = 1.0f;
                else
                    normal.z = 1.0f;
            }

            normal.x *= Math.signum(position.x);
            normal.y *= Math.signum(position.y);
            normal.z *= Math.signum(position.z);

            position.mulPosition(getWorldTransform());
            normal.mulDirection(getWorldTransform());

            return new Intersection(t, position, normal);
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
