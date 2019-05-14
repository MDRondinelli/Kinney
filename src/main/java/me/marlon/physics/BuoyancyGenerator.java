package me.marlon.physics;

import org.joml.Vector3f;

public class BuoyancyGenerator implements ForceGenerator {
    private Vector3f center;
    private float maxDepth;
    private float volume;
    private float k1;
    private float k2;
    private float fluidHeight;
    private float fluidDensity;

    public BuoyancyGenerator(Vector3f center, float maxDepth, float volume, float k1, float k2, float fluidHeight, float fluidDensity) {
        this.center = center;
        this.maxDepth = maxDepth;
        this.volume = volume;
        this.k1 = k1;
        this.k2 = k2;
        this.fluidHeight = fluidHeight;
        this.fluidDensity = fluidDensity;
    }

    public BuoyancyGenerator(Vector3f center, float maxDepth, float volume, float k1, float k2, float fluidHeight) {
        this(center, maxDepth, volume, k1, k2, fluidHeight, 1000.0f);
    }

    public void updateForce(RigidBody body, float dt) {
        if (!body.hasFiniteMass())
            return;

        Vector3f worldCenter = body.getTransform().transformPosition(center, new Vector3f());
        float depth = worldCenter.y;

        if (depth >= fluidHeight + maxDepth)
            return;

        Vector3f force = new Vector3f();

        if (depth <= fluidHeight - maxDepth)
            force.y = fluidDensity * volume;
        else
            force.y = fluidDensity * volume * (depth - maxDepth - fluidHeight) / (-2.0f * maxDepth);

        float dragCoeff = body.getVelocity().length();
        dragCoeff = fluidDensity * k1 * dragCoeff + k2 * dragCoeff * dragCoeff;

        force.add(body.getVelocity().normalize(-dragCoeff, new Vector3f()));
        body.addForceAtBodyPoint(force, center);
    }
}
