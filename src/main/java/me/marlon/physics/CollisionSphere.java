package me.marlon.physics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public class CollisionSphere extends CollisionPrimitive {
    private float radius;

    public CollisionSphere(RigidBody body, Vector3f transform, float radius) {
        super(body, new Matrix4f().translate(transform));
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

    public float getRadius() {
        return radius;
    }
}
