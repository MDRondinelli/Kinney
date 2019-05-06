package me.marlon.game;

import java.util.ArrayList;

public class EntityManager {
    public static final int MAX_ENTITIES = 1024;
    public static final int MAX_COMPONENTS = 16;

    public static final short CAMERA_BIT = 0x0001;
    public static final short MESH_BIT = 0x0002;
    public static final short PLAYER_BIT = 0x0004;
    public static final short TERRAIN_BIT = 0x0008;
    public static final short TRANSFORM_BIT = 0x0010;

    private ArrayList<Integer> freeList;

    private short[] entities;
    private CameraComponent[] cameraComponents;
    private MeshComponent[] meshComponents;
    private PlayerComponent[] playerComponents;
    private TerrainComponent[] terrainComponents;
    private TransformComponent[] transformComponents;

    public EntityManager() {
        freeList = new ArrayList<>(MAX_ENTITIES);
        for (int i = 0; i < MAX_ENTITIES; ++i)
            freeList.add(MAX_ENTITIES - 1 - i);

        entities = new short[MAX_ENTITIES];
        cameraComponents = new CameraComponent[MAX_ENTITIES];
        meshComponents = new MeshComponent[MAX_ENTITIES];
        playerComponents = new PlayerComponent[MAX_ENTITIES];
        terrainComponents = new TerrainComponent[MAX_ENTITIES];
        transformComponents = new TransformComponent[MAX_ENTITIES];
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

    public CameraComponent add(int i, CameraComponent component) {
        cameraComponents[i] = component;
        entities[i] |= CAMERA_BIT;
        return component;
    }

    public CameraComponent getCamera(int i) {
        return cameraComponents[i];
    }

    public MeshComponent add(int i, MeshComponent component) {
        meshComponents[i] = component;
        entities[i] |= MESH_BIT;
        return component;
    }

    public MeshComponent getMesh(int i) {
        return meshComponents[i];
    }

    public PlayerComponent add(int i, PlayerComponent component) {
        playerComponents[i] = component;
        entities[i] |= PLAYER_BIT;
        return component;
    }

    public PlayerComponent getPlayer(int i) {
        return playerComponents[i];
    }

    public TerrainComponent add(int i, TerrainComponent component) {
        terrainComponents[i] = component;
        entities[i] |= TERRAIN_BIT;
        return component;
    }

    public TerrainComponent getTerrain(int i) {
        return terrainComponents[i];
    }

    public TransformComponent add(int i, TransformComponent component) {
        transformComponents[i] = component;
        entities[i] |= TRANSFORM_BIT;
        return component;
    }

    public TransformComponent getTransform(int i) {
        return transformComponents[i];
    }

    public boolean match(int i, short bits) {
        return (entities[i] & bits) == bits;
    }
}
