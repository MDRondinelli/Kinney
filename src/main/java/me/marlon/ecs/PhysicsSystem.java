package me.marlon.ecs;

import me.marlon.physics.*;
import org.joml.AABBf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PhysicsSystem implements IComponentListener, IUpdateListener {
    private static final short DYNAMIC_BITS = EntityManager.RIGID_BODY_BIT | EntityManager.TRANSFORM_BIT;
    private static final short STATIC_BITS = EntityManager.COLLIDER_BIT | EntityManager.TRANSFORM_BIT;

    private EntityManager entities;
    private float deltaTime;

    private Set<Integer> dynamicIds;
    private Set<Integer> staticIds;

    private List<ForceRegistration> registry;
    private ContactResolver resolver;

    public PhysicsSystem(EntityManager entities, float deltaTime) {
        this.entities = entities;
        this.deltaTime = deltaTime;

        dynamicIds = new HashSet<>();
        staticIds = new HashSet<>();

        registry = new ArrayList<>();
        resolver = new ContactResolver(512, 0.01f, 512, 0.01f);
    }

    @Override
    public void onComponentAdded(int entity) {
        if (entities.match(entity, DYNAMIC_BITS))
            dynamicIds.add(entity);
        if (entities.match(entity, STATIC_BITS))
            staticIds.add(entity);
    }

    @Override
    public void onComponentRemoved(int entity) {
        if (!entities.match(entity, DYNAMIC_BITS))
            dynamicIds.remove(entity);
        if (!entities.match(entity, STATIC_BITS))
            staticIds.remove(entity);
    }

    @Override
    public void onUpdate() {
        List<RigidBody> bodies = new ArrayList<>();
        List<Collider> colliders = new ArrayList<>();

        for (int id : dynamicIds) {
            RigidBody body = entities.getRigidBody(id);
            body.clearAccumulators();
            body.updateDerivedData();
            bodies.add(body);
        }

        for (int id : staticIds) {
            Collider collider = entities.getCollider(id);
            collider.updateDerivedData(entities.getTransform(id).getMatrix());
            colliders.add(collider);
        }

        for (ForceRegistration registration : registry)
            registration.generator.updateForce(registration.body, deltaTime);

        for (RigidBody body : bodies)
            body.integrate(deltaTime);

        List<PotentialContact> potentialContacts = new ArrayList<>();

        for (int i = 0; i < bodies.size(); ++i) {
            RigidBody bodyOne = bodies.get(i);
            AABBf aabbOne = bodyOne.getCollider().getWorldAabb();

            for (int j = i + 1; j < bodies.size(); ++j) {
                RigidBody bodyTwo = bodies.get(j);
                AABBf aabbTwo = bodyTwo.getCollider().getWorldAabb();

                if ((bodyOne.hasFiniteMass() || bodyTwo.hasFiniteMass()) && aabbOne.testAABB(aabbTwo))
                    potentialContacts.add(new PotentialContact(bodyOne.getCollider(), bodyTwo.getCollider()));
            }

            if (bodyOne.hasFiniteMass()) {
                for (Collider bodyTwo : colliders) {
                    AABBf aabbTwo = bodyTwo.getWorldAabb();

                    if (aabbOne.testAABB(aabbTwo))
                        potentialContacts.add(new PotentialContact(bodyOne.getCollider(), bodyTwo));
                }
            }
        }

        List<Contact> contacts = new ArrayList<>();

        for (PotentialContact contact : potentialContacts)
            contact.getColliderA().collideWith(contact.getColliderB(), contacts);

        resolver.resolveContacts(contacts, deltaTime);

        for (int id : dynamicIds) {
            RigidBody body = entities.getRigidBody(id);
            TransformComponent transform = entities.getTransform(id);
            transform.setPosition(body.getPosition());
            transform.setOrientation(body.getOrientation());
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

    public float rayCast(Vector3f o, Vector3f d) {
        float tMin = Float.MAX_VALUE;

        for (int id : dynamicIds) {
            if (entities.match(id, EntityManager.PLAYER_BIT))
                continue;

            Float t = entities.getRigidBody(id).getCollider().rayCast(o, d);
            if (t != null && 0.0f < t && t < tMin)
                tMin = t;
        }

        for (int id : staticIds) {
            if (entities.match(id, EntityManager.PLAYER_BIT))
                continue;

            Float t = entities.getCollider(id).rayCast(o, d);
            if (t != null && 0.0f < t && t < tMin)
                tMin = t;
        }

        return tMin;
    }
}
