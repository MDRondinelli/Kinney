package me.marlon.game;

import me.marlon.ecs.*;
import me.marlon.gfx.*;
import me.marlon.physics.PhysicsMaterial;
import me.marlon.physics.RigidBody;
import org.joml.Vector3f;

public class Main {
    public static void main(String[] args) {
        Engine engine = new Engine(1280, 720, "Kinney", 1.0f / 60.0f);
        engine.getWindow().setMouseGrabbed(true);

        EntityManager entities = engine.getEntities();

        int terrainEntity = entities.create();
        Terrain terrain = entities.add(terrainEntity, new Terrain(400));
        entities.add(terrainEntity, RigidBody.createTerrain(terrain));
        entities.add(terrainEntity, new TransformComponent());

        int player = entities.create();
        entities.add(player, new Camera((float) Math.toRadians(55.0f), 16.0f / 9.0f, 0.15f, 120.0f));
        entities.add(player, new Player(4.0f));

        Vector3f playerPos = new Vector3f(200.0f, 0.0f, 200.0f);
        playerPos.y = terrain.sample(playerPos.x, playerPos.z) + 2.0f;

        RigidBody playerBody = RigidBody.createCuboid(PhysicsMaterial.PLAYER, new Vector3f(0.25f, 1.0f, 0.25f), 1.0f / 50.0f, playerPos);
        playerBody.getInvInertiaTensor().zero();
        playerBody.getAcceleration().y = -10.0f;

        entities.add(player, playerBody);
        entities.add(player, new TransformComponent());

//        RigidBody playerBody = RigidBody.createCuboid(new Vector3f(0.1f, 1.0f, 0.1f), 1.0f / 200.0f, new Vector3f(200.0f, terrain.sample(200.0f, 200.0f) + 2.0f, 200.0f));
//        playerBody.getAcceleration().y = -10.0f;
//        playerBody.getInvInertiaTensor().zero();
//        entities.add(player, playerBody);

        int water = entities.create();
        entities.add(water, new WaterMesh(1024));
        entities.add(water, new TransformComponent().translate(new Vector3f(-512.0f, 4.0f, -512.0f)));

        int sun = entities.create();
        entities.add(sun, new DirectionalLight(new Vector3f(1.0f), new Vector3f(1.25f, -1.5f, 1.0f).normalize()));

        engine.run();
        engine.close();
    }
}
