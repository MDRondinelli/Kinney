package me.marlon.game;

import me.marlon.ecs.*;
import me.marlon.gfx.*;
import me.marlon.physics.BuoyancyGenerator;
import me.marlon.physics.RigidBody;
import org.joml.Vector3f;

public class Main {
    public static void main(String[] args) {
        Engine engine = new Engine(1280, 720, "Kinney", 1.0f / 60.0f);
        engine.getWindow().setMouseGrabbed(true);

        World world = engine.getWorld();
        EntityManager entities = world.getEntities();

        int player = entities.create();
        entities.add(player, new Camera((float) Math.toRadians(55.0f), 16.0f / 9.0f, 0.4f, 120.0f));
        entities.add(player, new Player(4.0f));

        RigidBody playerBody = RigidBody.createSphere(1.0f, 1.0f / 200.0f, new Vector3f(200.0f, 40.0f, 200.0f));
        playerBody.setAcceleration(new Vector3f(0.0f, -10.0f, 0.0f));
        playerBody.getInvInertiaTensor().zero();
        BuoyancyGenerator playerBuoyancy = new BuoyancyGenerator(new Vector3f(), 1.0f, 4.0f / 3.0f * (float) Math.PI, 3.0f, 10.0f, 4.0f);
        world.getPhysicsSystem().register(playerBuoyancy, playerBody);

        entities.add(player, playerBody);
        entities.add(player, new TransformComponent());

        int terrainEntity = entities.create();
        Terrain terrain = entities.add(terrainEntity, new Terrain(400));
        entities.add(terrainEntity, RigidBody.createTerrain(terrain));
        entities.add(terrainEntity, new TransformComponent());

        int water = entities.create();
        entities.add(water, new WaterMesh(1024));
        entities.add(water, new TransformComponent().translate(new Vector3f(-512.0f, 4.0f, -512.0f)));

        int sun = entities.create();
        entities.add(sun, new DirectionalLight(new Vector3f(1.0f), new Vector3f(1.0f, -1.0f, 1.0f).normalize()));

        engine.run();
        engine.close();
    }
}
