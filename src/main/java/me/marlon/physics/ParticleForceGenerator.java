package me.marlon.physics;

public interface ParticleForceGenerator {
    void updateForce(Particle particle, float dt);
}
