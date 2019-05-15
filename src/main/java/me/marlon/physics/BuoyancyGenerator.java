package me.marlon.physics;

import org.joml.Vector3f;

public class BuoyancyGenerator implements ForceGenerator {
    private Vector3f center;
    private float maxDepth;
    private float volume;
    private float cd;
    private float gravity;
    private float fluidHeight;
    private float fluidDensity;

    public BuoyancyGenerator(Vector3f center, float maxDepth, float volume, float cd, float gravity, float fluidHeight, float fluidDensity) {
        this.center = center;
        this.maxDepth = maxDepth;
        this.volume = volume;
        this.cd = cd;
        this.gravity = gravity;
        this.fluidHeight = fluidHeight;
        this.fluidDensity = fluidDensity;
    }

    public BuoyancyGenerator(Vector3f center, float maxDepth, float volume, float cd, float gravity, float fluidHeight) {
        this(center, maxDepth, volume, cd, gravity, fluidHeight, 1000.0f);
    }

    public void updateForce(RigidBody body, float dt) {
        if (!body.hasFiniteMass())
            return;

        Vector3f worldCenter = body.getTransform().transformPosition(center, new Vector3f());
        float depth = worldCenter.y;

        if (depth >= fluidHeight + maxDepth)
            return;

        Vector3f force = new Vector3f();
        float factor;

        if (depth <= fluidHeight - maxDepth) {
            factor = 1.0f;
            force.y = fluidDensity * volume * gravity;
        } else {
            factor = (depth - maxDepth - fluidHeight) / (-2.0f * maxDepth);
            force.y = fluidDensity * volume * gravity * factor;
        }

        Vector3f rotation = new Vector3f(body.getRotation());
        if (rotation.lengthSquared() != 0.0f)
            body.addTorque(rotation.normalize(0.5f * fluidDensity * -rotation.lengthSquared() * cd * factor));

        Vector3f velocity = new Vector3f(body.getVelocity());
        force.add(velocity.normalize(0.5f * fluidDensity * -velocity.lengthSquared() * cd * factor));
        body.addForceAtBodyPoint(force, center);
    }
}
