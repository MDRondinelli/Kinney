package me.marlon.game;

import me.marlon.ecs.*;
import me.marlon.gfx.DirectionalLight;
import me.marlon.gfx.TerrainMesh;
import me.marlon.gfx.WaterMesh;
import org.joml.Vector3f;

public class Main {
    public static void main(String[] args) {
        Engine engine = new Engine(1280, 720, "Kinney", 1.0f / 60.0f);
        engine.getWindow().setMouseGrabbed(true);

        World world = engine.getWorld();
        EntityManager entities = world.getEntities();

        int player = entities.create();
        entities.add(player, new Camera((float) Math.toRadians(55.0f), 16.0f / 9.0f, 0.25f, 200.0f));
        entities.add(player, new Player(20.0f));
        entities.add(player, new TransformComponent()).translate(new Vector3f(128.0f, 2.0f, -128.0f));

        int terrain = entities.create();
        entities.add(terrain, new TerrainMesh(512));

        int water = entities.create();
        entities.add(water, new WaterMesh(512));

        int sun = entities.create();
        entities.add(sun, new DirectionalLight(new Vector3f(1.0f), new Vector3f(1.0f, -2.0f, -1.0f).normalize()));

        engine.run();
        engine.close();
    }
}
