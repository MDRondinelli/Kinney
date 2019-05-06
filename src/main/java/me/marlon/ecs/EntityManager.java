package me.marlon.ecs;

import me.marlon.gfx.DirectionalLight;
import me.marlon.gfx.Mesh;
import me.marlon.gfx.Terrain;
import me.marlon.physics.Particle;

import java.util.ArrayList;

public class EntityManager {
    public static final int MAX_ENTITIES = 1024;
    public static final int MAX_COMPONENTS = 16;

    public static final short CAMERA_BIT = 0x0001;
    public static final short DLIGHT_BIT = 0x0002;
    public static final short MESH_BIT = 0x0004;
    public static final short PARTICLE_BIT = 0x0008;
    public static final short PLAYER_BIT = 0x0010;
    public static final short TERRAIN_BIT = 0x0020;
    public static final short TRANSFORM_BIT = 0x0040;

    private ArrayList<Integer> freeList;

    private short[] entities;
    private Camera[] cameras;
    private DirectionalLight[] dLights;
    private Mesh[] meshes;
    private Particle[] particles;
    private Player[] players;
    private Terrain[] terrains;
    private TransformComponent[] transforms;

    public EntityManager() {
        freeList = new ArrayList<>(MAX_ENTITIES);
        for (int i = 0; i < MAX_ENTITIES; ++i)
            freeList.add(MAX_ENTITIES - 1 - i);

        entities = new short[MAX_ENTITIES];
        cameras = new Camera[MAX_ENTITIES];
        dLights = new DirectionalLight[MAX_ENTITIES];
        meshes = new Mesh[MAX_ENTITIES];
        particles = new Particle[MAX_ENTITIES];
        players = new Player[MAX_ENTITIES];
        terrains = new Terrain[MAX_ENTITIES];
        transforms = new TransformComponent[MAX_ENTITIES];
    }

    public int create() {
        int i = freeList.remove(freeList.size() - 1);
        entities[i] = 0;
        return i;
    }

    public void destroy(int i) {
        freeList.add(i);
        entities[i] = 0;
    }

    public Camera add(int i, Camera component) {
        cameras[i] = component;
        entities[i] |= CAMERA_BIT;
        return component;
    }

    public Camera getCamera(int i) {
        return cameras[i];
    }

    public DirectionalLight add(int i, DirectionalLight component) {
        dLights[i] = component;
        entities[i] |= DLIGHT_BIT;
        return component;
    }

    public DirectionalLight getDLight(int i) {
        return dLights[i];
    }

    public Mesh add(int i, Mesh component) {
        meshes[i] = component;
        entities[i] |= MESH_BIT;
        return component;
    }

    public Mesh getMesh(int i) {
        return meshes[i];
    }

    public Particle add(int i, Particle component) {
        particles[i] = component;
        entities[i] |= PARTICLE_BIT;
        return component;
    }

    public Particle getParticle(int i) {
        return particles[i];
    }

    public Player add(int i, Player component) {
        players[i] = component;
        entities[i] |= PLAYER_BIT;
        return component;
    }

    public Player getPlayer(int i) {
        return players[i];
    }

    public Terrain add(int i, Terrain component) {
        terrains[i] = component;
        entities[i] |= TERRAIN_BIT;
        return component;
    }

    public Terrain getTerrain(int i) {
        return terrains[i];
    }

    public TransformComponent add(int i, TransformComponent component) {
        transforms[i] = component;
        entities[i] |= TRANSFORM_BIT;
        return component;
    }

    public TransformComponent getTransform(int i) {
        return transforms[i];
    }

    public boolean match(int i, short bits) {
        return (entities[i] & bits) == bits;
    }
}
