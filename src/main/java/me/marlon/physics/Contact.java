package me.marlon.physics;

import org.joml.Vector3f;

public class Contact {
    private Vector3f point;
    private Vector3f normal;
    private float depth;

    private RigidBody bodyA;
    private RigidBody bodyB;

    public Contact(Vector3f point, Vector3f normal, float depth, RigidBody bodyA, RigidBody bodyB) {
        this.point = point;
        this.normal = normal;
        this.depth = depth;
        this.bodyA = bodyA;
        this.bodyB = bodyB;
    }

    public Vector3f getPoint() {
        return point;
    }

    public void setPoint(Vector3f point) {
        this.point = point;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public void setNormal(Vector3f normal) {
        this.normal = normal;
    }

    public float getDepth() {
        return depth;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }

    public RigidBody getBodyA() {
        return bodyA;
    }

    public void setBodyA(RigidBody bodyA) {
        this.bodyA = bodyA;
    }

    public RigidBody getBodyB() {
        return bodyB;
    }

    public void setBodyB(RigidBody bodyB) {
        this.bodyB = bodyB;
    }
}
