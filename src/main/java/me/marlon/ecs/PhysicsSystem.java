package me.marlon.ecs;

import me.marlon.physics.*;
import org.joml.AABBf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class PhysicsSystem {
    private static final short BITS = EntityManager.RIGID_BODY_BIT | EntityManager.TRANSFORM_BIT;

    private EntityManager entities;
    private float deltaTime;

    private List<ForceRegistration> registry;
    private ContactResolver resolver;

    public PhysicsSystem(EntityManager entities, float deltaTime) {
        this.entities = entities;
        this.deltaTime = deltaTime;

        registry = new ArrayList<>();
        resolver = new ContactResolver(512, 0.01f, 512, 0.01f);
    }

    public void onUpdate() {
        List<Integer> bodies = new ArrayList<>();

        for (int i = 0; i < EntityManager.MAX_ENTITIES; ++i) {
            if (!entities.match(i, BITS))
                continue;

            entities.getRigidBody(i).clearAccumulators();
            entities.getRigidBody(i).updateDerivedData();
            bodies.add(i);
        }

        for (int i = 0; i < registry.size(); ++i) {
            ForceRegistration registration = registry.get(i);
            registration.generator.updateForce(registration.body, deltaTime);
        }

        for (int i = 0; i < bodies.size(); ++i)
            entities.getRigidBody(bodies.get(i)).integrate(deltaTime);

        List<PotentialContact> potentialContacts = new ArrayList<>();

        for (int i = 0; i < bodies.size(); ++i) {
            RigidBody bodyOne = entities.getRigidBody(bodies.get(i));
            AABBf aabbOne = bodyOne.getTransformVolume();

            for (int j = i + 1; j < bodies.size(); ++j) {
                RigidBody bodyTwo = entities.getRigidBody(bodies.get(j));
                AABBf aabbTwo = bodyTwo.getTransformVolume();

                if (aabbOne.testAABB(aabbTwo))
                    potentialContacts.add(new PotentialContact(bodyOne, bodyTwo));
            }
        }

        List<Contact> contacts = new ArrayList<>();

        for (int i = 0; i < potentialContacts.size(); ++i) {
            PotentialContact potentialContact = potentialContacts.get(i);
            potentialContact.getBodyA().getCollider().collideWith(potentialContact.getBodyB().getCollider(), contacts);
        }

        resolver.resolveContacts(contacts, deltaTime);

//        for (int i = 0; i < contacts.size(); ++i)
//            contacts.get(i).resolve();

        for (int i = 0; i < EntityManager.MAX_ENTITIES; ++i) {
            if (!entities.match(i, BITS))
                continue;

            RigidBody body = entities.getRigidBody(i);
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
}
