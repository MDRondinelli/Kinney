package me.marlon.physics;

import java.util.ArrayList;

public class ParticleForceRegistry {
    private static class Registration {
        public Particle particle;
        public ParticleForceGenerator generator;

        public Registration(Particle particle, ParticleForceGenerator generator) {
            this.particle = particle;
            this.generator = generator;
        }
    }

    private ArrayList<Registration> registry;

    public ParticleForceRegistry() {
        registry = new ArrayList<>();
    }

    public void add(Particle particle, ParticleForceGenerator generator) {
        registry.add(new Registration(particle, generator));
    }

    public void remove(Particle particle, ParticleForceGenerator generator) {
        for (int i = 0; i < registry.size(); ++i) {
            Registration registration = registry.get(i);
            if (registration.particle == particle && registration.generator == generator)
                registry.remove(i--);
        }
    }

    public void clear() {
        registry.clear();
    }

    public void updateForces(float dt) {
        for (int i = 0; i < registry.size(); ++i) {
            Registration registration = registry.get(i);
            registration.generator.updateForce(registration.particle, dt);
        }
    }
}
