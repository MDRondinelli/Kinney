package me.marlon.physics;

public interface ForceGenerator {
    void updateForce(RigidBody body, float dt);
}
