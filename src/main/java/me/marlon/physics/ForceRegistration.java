package me.marlon.physics;

public class ForceRegistration {
    public ForceGenerator generator;
    public RigidBody body;

    public ForceRegistration(ForceGenerator generator, RigidBody body) {
        this.generator = generator;
        this.body = body;
    }
}
