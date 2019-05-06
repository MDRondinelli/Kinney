package me.marlon.ecs;

import me.marlon.physics.Particle;

public class ParticleSystem {
    public static final short BITS = EntityManager.PARTICLE_BIT | EntityManager.TRANSFORM_BIT;

    private EntityManager entities;
    private float deltaTime;

    public ParticleSystem(EntityManager entities, float deltaTime) {
        this.entities = entities;
        this.deltaTime = deltaTime;
    }

    public void onUpdate() {
        for (int i = 0; i < EntityManager.MAX_ENTITIES; ++i) {
            if (!entities.match(i, BITS))
                continue;

            Particle particle = entities.getParticle(i);
            particle.integrate(deltaTime);

            entities.getTransform(i).setPosition(particle.getPosition());
        }
    }
}
