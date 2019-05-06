package me.marlon.game;

import me.marlon.gfx.Mesh;
import me.marlon.gfx.Primitive;
import me.marlon.gfx.Terrain;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Main {
    public static void main(String[] args) {
        Engine engine = new Engine(1280, 720, "Kinney", 1.0f / 60.0f);
        engine.getWindow().setMouseGrabbed(true);

        World world = engine.getWorld();
        EntityManager entities = world.getEntities();

        int player = entities.create();
        entities.add(player, new CameraComponent((float) Math.toRadians(55.0f), 16.0f / 9.0f, 0.25f, 200.0f));
        entities.add(player, new PlayerComponent(4.0f));
        entities.add(player, new TransformComponent()).translate(new Vector3f(256.0f, 2.0f, -256.0f));

        int terrain = entities.create();
        entities.add(terrain, new TerrainComponent(new Terrain(512)));

        engine.run();
        engine.close();
    }
}
