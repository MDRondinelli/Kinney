package me.marlon.game;

import me.marlon.ecs.*;
import me.marlon.gfx.*;
import me.marlon.physics.Intersection;
import me.marlon.physics.PhysicsMaterial;
import me.marlon.physics.RigidBody;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Engine engine = new Engine(1280, 720, "Kinney", 1.0f / 60.0f);
        engine.getWindow().setMouseGrabbed(true);

        BlockSystem blocks = engine.getBlockSystem();
        PhysicsSystem physics = engine.getPhysicsSystem();

        EntityManager entities = engine.getEntities();
        ItemManager items = engine.getItems();
        items.add(new Item("Item A"));
        items.add(new Item("Item B"));

        items.add(new ItemBlock("Test Block", entities, blocks, physics, "res/meshes/box.obj", new BlockFactory() {
            public Block create(Item item, int x, int y, int z) {
                return new Block(item, false, x, y, z);
            }
        }));

        items.add(new ItemBlock("Chest", entities, blocks, physics, "res/meshes/chest.obj", new BlockFactory() {
            public Block create(Item item, int x, int y, int z) {
                return new BlockChest(item, x, y, z, entities, engine.getGui());
            }
        }));

        items.add(new ItemBlock("Smelter", entities, blocks, physics, "res/meshes/smelter.obj", new BlockFactory() {
            public Block create(Item item, int x, int y, int z) {
                return new BlockChest(item, x, y, z, entities, engine.getGui());
            }
        }));

        int terrainEntity = entities.create();
        Terrain terrain = entities.add(terrainEntity, new Terrain(400));
        entities.add(terrainEntity, RigidBody.createTerrain(terrain));
        entities.add(terrainEntity, new TransformComponent());

        int playerEntity = entities.create();
        entities.add(playerEntity, new Camera((float) Math.toRadians(55.0f), 16.0f / 9.0f, 0.2f, 120.0f));

        Player player = new Player(4.0f, 4.0f);
        player.inventory.add(items.get("Item A"), 420);
        player.inventory.add(items.get("Item A"), 69);
        player.inventory.add(items.get("Item B"), 21);
        player.inventory.add(items.get("Test Block"), 64);
        player.inventory.add(items.get("Chest"), 64);
        player.inventory.add(items.get("Smelter"), 64);

        entities.add(playerEntity, player);

        Vector3f playerPos = new Vector3f(200.0f, 0.0f, 200.0f);
        playerPos.y = terrain.sample(playerPos.x, playerPos.z) + 2.0f;

        RigidBody playerBody = RigidBody.createCuboid(PhysicsMaterial.PLAYER, new Vector3f(0.25f, 1.0f, 0.25f), 1.0f / 50.0f, playerPos);
        playerBody.getInvInertiaTensor().zero();
        playerBody.getAcceleration().y = -10.0f;

        entities.add(playerEntity, playerBody);
        entities.add(playerEntity, new TransformComponent());

        int water = entities.create();
        entities.add(water, new WaterMesh(1024));
        entities.add(water, new TransformComponent().translate(new Vector3f(-512.0f, 4.0f, -512.0f)));

        int sun = entities.create();
        entities.add(sun, new DirectionalLight(new Vector3f(1.0f), new Vector3f(1.25f, -1.5f, 1.0f).normalize()));

        Mesh ore = null;

        try {
            ore = new Mesh(new Primitive("res/meshes/ore.obj", new Vector3f(0.05f)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 256; ++i) {
            int oreEntity = entities.create();
            entities.add(oreEntity, ore);
            float x = (float) Math.random() * 200.0f;
            float z = (float) Math.random() * 200.0f;

            Intersection isect = physics.rayCast(new Vector3f(x, 50.0f, z), new Vector3f(0.0f, -1.0f, 0.0f));
            if (isect != null) {
                Vector3f position = isect.getPosition();
                Vector3f axis = new Vector3f(isect.getNormal()).cross(0.0f, 1.0f, 0.0f).negate();
//                float angle = (float) Math.acos(isect.getNormal().dot(0.0f, 1.0f, 0.0f));
                Quaternionf orientation = new Quaternionf();
                orientation.x = axis.x;
                orientation.y = axis.y;
                orientation.z = axis.z;
                orientation.w = 1.0f + isect.getNormal().dot(0.0f, 1.0f, 0.0f);
                orientation.normalize();

                entities.add(oreEntity, new TransformComponent().translate(position).rotate(orientation));
            }
        }

        engine.run();
        engine.close();
    }
}
