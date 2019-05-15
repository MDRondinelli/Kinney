package me.marlon.physics;

public class PotentialContact {
    private RigidBody bodyA;
    private RigidBody bodyB;

    public PotentialContact(RigidBody bodyA, RigidBody bodyB) {
        this.bodyA = bodyA;
        this.bodyB = bodyB;
    }

    public RigidBody getBodyA() {
        return bodyA;
    }

    public RigidBody getBodyB() {
        return bodyB;
    }
}
