package me.marlon.physics;

import org.joml.Vector3f;

public class GravityGenerator implements ForceGenerator {
    private Vector3f gravity;

    public GravityGenerator(Vector3f gravity) {
        this.gravity = gravity;
    }

    public void updateForce(RigidBody body, float dt) {
        if (body.hasFiniteMass())
            body.addForce(gravity.mul(body.getMass(), new Vector3f()));
    }

    public Vector3f getGravity() {
        return gravity;
    }

    public void setGravity(Vector3f gravity) {
        this.gravity = gravity;
    }
}
