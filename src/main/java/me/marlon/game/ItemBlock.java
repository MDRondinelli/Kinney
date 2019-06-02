package me.marlon.game;

import me.marlon.ecs.*;
import me.marlon.gfx.Mesh;
import me.marlon.physics.CollisionBox;
import me.marlon.physics.PhysicsMaterial;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ItemBlock extends Item {
    private EntityManager entities;
    private PhysicsSystem physicsSystem;
    private Mesh mesh;

    public ItemBlock(String name, EntityManager entities, PhysicsSystem physicsSystem, Mesh mesh) {
        super(name);
        this.entities = entities;
        this.physicsSystem = physicsSystem;
        this.mesh = mesh;
    }

    @Override
    public boolean use(int user) {
        TransformComponent userTransform = entities.getTransform(user);
        Vector3f o = new Vector3f(userTransform.getPosition());
        Vector3f d = new Vector3f(0.0f, 0.0f, -1.0f).rotate(userTransform.getOrientation());

        float t = physicsSystem.rayCast(o, d);

        if (t < 3.5f) {
            t -= 0.01f;

            int x = (int) (o.x + d.x * t);
            int y = (int) (o.y + d.y * t);
            int z = (int) (o.z + d.z * t);

            CollisionBox collider = new CollisionBox(PhysicsMaterial.CONCRETE, new Matrix4f(), new Vector3f(0.5f));
            collider.updateDerivedData(new Matrix4f().translate(x + 0.5f, y + 0.5f, z + 0.5f));

            if (!collider.collideWith(entities.getRigidBody(user).getCollider())) {
                int block = entities.create();
                entities.add(block, mesh);
                entities.add(block, collider);
                entities.add(block, new Block(this, x, y, z));
                entities.add(block, new TransformComponent());

                return true;
            }
        }

        return false;
    }
}
