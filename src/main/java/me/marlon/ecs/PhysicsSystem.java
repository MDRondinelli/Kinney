package me.marlon.ecs;

import me.marlon.physics.ForceGenerator;
import me.marlon.physics.ForceRegistration;
import me.marlon.physics.GravityGenerator;
import me.marlon.physics.RigidBody;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class PhysicsSystem {
    private static final short BITS = EntityManager.RIGID_BODY_BIT | EntityManager.TRANSFORM_BIT;

    private EntityManager entities;
    private float deltaTime;

    private GravityGenerator gravity;
    private List<ForceRegistration> registry;

    public PhysicsSystem(EntityManager entities, float deltaTime) {
        this.entities = entities;
        this.deltaTime = deltaTime;

        gravity = new GravityGenerator(new Vector3f(0.0f, -12.0f, 0.0f));
        registry = new ArrayList<>();
    }

    public void onUpdate() {
        for (int i = 0; i < EntityManager.MAX_ENTITIES; ++i) {
            if (!entities.match(i, BITS))
                continue;

            RigidBody body = entities.getRigidBody(i);
            body.clearAccumulators();
            body.updateDerivedData();
        }

        for (int i = 0; i < registry.size(); ++i) {
            ForceRegistration registration = registry.get(i);
            registration.generator.updateForce(registration.body, deltaTime);
        }

        for (int i = 0; i < EntityManager.MAX_ENTITIES; ++i) {
            if (!entities.match(i, BITS))
                continue;

            RigidBody body = entities.getRigidBody(i);
            body.integrate(deltaTime);

            TransformComponent transform = entities.getTransform(i);
            transform.setPosition(body.getPosition());
            transform.setRotation(body.getOrientation());
        }
    }

    public void register(ForceGenerator generator, RigidBody body) {
        registry.add(new ForceRegistration(generator, body));
    }

    public void unregister(ForceGenerator generator, RigidBody body) {
        for (int i = 0; i < registry.size(); ++i) {
            ForceRegistration registration = registry.get(i);
            if (registration.generator == generator && registration.body == body)
                registry.remove(i--);
        }
    }

    public GravityGenerator getGravity() {
        return gravity;
    }
}
