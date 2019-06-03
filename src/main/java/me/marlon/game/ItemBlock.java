package me.marlon.game;

import me.marlon.ecs.*;
import me.marlon.gfx.Mesh;
import me.marlon.gfx.Primitive;
import me.marlon.physics.CollisionBox;
import me.marlon.physics.PhysicsMaterial;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;

public class ItemBlock extends Item {
    private EntityManager entities;
    private BlockSystem blocks;
    private PhysicsSystem physics;

    private Mesh mesh;
    private BlockFactory factory;

    public ItemBlock(String name, EntityManager entities, BlockSystem blocks, PhysicsSystem physics, Mesh mesh, BlockFactory factory) {
        super(name);
        this.entities = entities;
        this.blocks = blocks;
        this.physics = physics;
        this.mesh = mesh;
        this.factory = factory;
    }

    public ItemBlock(String name, EntityManager entities, BlockSystem blocks, PhysicsSystem physics, String meshPath, BlockFactory factory) {
        super(name);
        this.entities = entities;
        this.blocks = blocks;
        this.physics = physics;

        try {
            this.mesh = new Mesh(new Primitive(meshPath, new Vector3f(0.5f)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.factory = factory;
    }

    @Override
    public boolean use(int user) {
        TransformComponent userTransform = entities.getTransform(user);
        Vector3f o = new Vector3f(userTransform.getPosition());
        Vector3f d = new Vector3f(0.0f, 0.0f, -1.0f).rotate(userTransform.getOrientation());

        Player userPlayer = entities.getPlayer(user);

        float t = physics.rayCast(o, d);
        if (t < userPlayer.reach) {
            int x = (int) (o.x + d.x * (t - 0.01f));
            int y = (int) (o.y + d.y * (t - 0.01f));
            int z = (int) (o.z + d.z * (t - 0.01f));

            Block block = factory.create(this, x, y, z);

            if (block.isFunctional() && blocks.getBlock(x, y - 1, z) == 0xffffffff)
                return false;

            CollisionBox collider = new CollisionBox(PhysicsMaterial.CONCRETE, new Matrix4f(), new Vector3f(0.5f));
            collider.updateDerivedData(new Matrix4f().translate(x + 0.5f, y + 0.5f, z + 0.5f));

            if (!collider.collideWith(entities.getRigidBody(user).getCollider())) {
                int e = entities.create();
                entities.add(e, mesh);
                entities.add(e, collider);
                entities.add(e, block);
                entities.add(e, new TransformComponent());

                return true;
            }
        }

        return false;
    }
}
