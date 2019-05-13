package me.marlon.physics;

import org.joml.Vector3f;

public class BuoyancyGenerator implements ForceGenerator {
    private Vector3f center;
    private float maxDepth;
    private float volume;
    private float fluidHeight;
    private float fluidDensity;

    public BuoyancyGenerator(Vector3f center, float maxDepth, float volume, float fluidHeight, float fluidDensity) {
        this.center = center;
        this.maxDepth = maxDepth;
        this.volume = volume;
        this.fluidHeight = fluidHeight;
        this.fluidDensity = fluidDensity;
    }

    public BuoyancyGenerator(Vector3f center, float maxDepth, float volume, float fluidHeight) {
        this(center, maxDepth, volume, fluidHeight, 1000.0f);
    }

    public void updateForce(RigidBody body, float dt) {
        if (!body.hasFiniteMass())
            return;

        Vector3f worldCenter = body.getTransform().transformPosition(center, new Vector3f());
        float depth = worldCenter.y;

        if (depth >= fluidHeight + maxDepth)
            return;

        Vector3f force = new Vector3f();

        if (depth <= fluidHeight - maxDepth) {
            force.y = fluidDensity * volume;
            body.addForceAtBodyPoint(force, center);
        } else {
            force.y = fluidDensity * volume * (depth - maxDepth - fluidHeight) / (2.0f * maxDepth);
            body.addForceAtBodyPoint(force, center);
        }
    }
}
