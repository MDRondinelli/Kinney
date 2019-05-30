package me.marlon.physics;

import org.joml.AABBf;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public class CollisionSphere extends CollisionPrimitive {
    private float radius;

    public CollisionSphere(Vector3f transform, float radius) {
        super(new Matrix4f().translate(transform), new AABBf(-radius, -radius, -radius, radius, radius, radius));
        this.radius = radius;
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
        Vector3f oc = new Vector3f(o).sub(getWorldTransform().m30(), getWorldTransform().m31(), getWorldTransform().m32());

        float b = oc.dot(d);
        float c = oc.dot(oc) - radius * radius;
        float h = b * b - c;
        if (h < 0.0f)
            return null;

        h = (float) Math.sqrt(h);

        float tN = -b - h;
        float tF = -b + h;

        if (tN > 0.0f)
            return tN;
        else if (tF > 0.0f)
            return tF;
        else
            return null;
    }

    public float getRadius() {
        return radius;
    }
}
