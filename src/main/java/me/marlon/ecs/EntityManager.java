package me.marlon.ecs;

import me.marlon.game.Inventory;
import me.marlon.gfx.*;
import me.marlon.physics.Collider;
import me.marlon.physics.RigidBody;

import java.util.ArrayList;
import java.util.List;

public class EntityManager {
    private static final int MAX_ENTITIES = 2048;

    public static final int BLOCK_BIT       = 0x00000001;
    public static final int CAMERA_BIT      = 0x00000002;
    public static final int COLLIDER_BIT    = 0x00000004;
    public static final int DLIGHT_BIT      = 0x00000008;
    public static final int MESH_BIT        = 0x00000010;
    public static final int PLAYER_BIT      = 0x00000020;
    public static final int RIGID_BODY_BIT  = 0x00000040;
    public static final int TERRAIN_BIT     = 0x00000080;
    public static final int TRANSFORM_BIT   = 0x00000100;
    public static final int WATER_MESH_BIT  = 0x00000200;

    private ArrayList<Integer> freeList;

    private int[] entities;

    private Block[] blocks;
    private Camera[] cameras;
    private Collider[] colliders;
    private DirectionalLight[] dLights;
    private Mesh[] meshes;
    private Player[] players;
    private RigidBody[] rigidBodies;
    private Terrain[] terrains;
    private TransformComponent[] transforms;
    private WaterMesh[] waterMeshes;

    private List<IComponentListener> componentListeners;

    public EntityManager() {
        freeList = new ArrayList<>(MAX_ENTITIES);
        for (int i = 0; i < MAX_ENTITIES; ++i)
            freeList.add(MAX_ENTITIES - 1 - i);

        entities = new int[MAX_ENTITIES];
        blocks = new Block[MAX_ENTITIES];
        cameras = new Camera[MAX_ENTITIES];
        colliders = new Collider[MAX_ENTITIES];
        dLights = new DirectionalLight[MAX_ENTITIES];
        meshes = new Mesh[MAX_ENTITIES];
        players = new Player[MAX_ENTITIES];
        rigidBodies = new RigidBody[MAX_ENTITIES];
        terrains = new Terrain[MAX_ENTITIES];
        transforms = new TransformComponent[MAX_ENTITIES];
        waterMeshes = new WaterMesh[MAX_ENTITIES];

        componentListeners = new ArrayList<>();
    }

    public void addComponentListener(IComponentListener listener) {
        componentListeners.add(listener);
    }

    public int create() {
        int i = freeList.remove(freeList.size() - 1);
        entities[i] = 0;
        return i;
    }

    public void destroy(int i) {
        freeList.add(i);
        entities[i] = 0;
        blocks[i] = null;
        cameras[i] = null;
        colliders[i] = null;
        dLights[i] = null;
        meshes[i] = null;
        players[i] = null;
        rigidBodies[i] = null;
        terrains[i] = null;
        transforms[i] = null;
        waterMeshes[i] = null;

        for (IComponentListener listener : componentListeners)
            listener.onComponentRemoved(i);
    }

    public Block add(int i, Block component) {
        blocks[i] = component;
        entities[i] |= BLOCK_BIT;

        for (IComponentListener listener : componentListeners)
            listener.onComponentAdded(i);

        return component;
    }

    public Block getBlock(int i) {
        return blocks[i];
    }

    public Camera add(int i, Camera component) {
        cameras[i] = component;
        entities[i] |= CAMERA_BIT;

        for (IComponentListener listener : componentListeners)
            listener.onComponentAdded(i);

        return component;
    }

    public Camera getCamera(int i) {
        return cameras[i];
    }

    public Collider add(int i, Collider component) {
        colliders[i] = component;
        entities[i] |= COLLIDER_BIT;

        for (IComponentListener listener : componentListeners)
            listener.onComponentAdded(i);

        return component;
    }

    public Collider getCollider(int i) {
        return colliders[i];
    }

    public DirectionalLight add(int i, DirectionalLight component) {
        dLights[i] = component;
        entities[i] |= DLIGHT_BIT;

        for (IComponentListener listener : componentListeners)
            listener.onComponentAdded(i);

        return component;
    }

    public DirectionalLight getDLight(int i) {
        return dLights[i];
    }

    public Mesh add(int i, Mesh component) {
        meshes[i] = component;
        entities[i] |= MESH_BIT;

        for (IComponentListener listener : componentListeners)
            listener.onComponentAdded(i);

        return component;
    }

    public Mesh getMesh(int i) {
        return meshes[i];
    }

    public Player add(int i, Player component) {
        players[i] = component;
        entities[i] |= PLAYER_BIT;

        for (IComponentListener listener : componentListeners)
            listener.onComponentAdded(i);

        return component;
    }

    public Player getPlayer(int i) {
        return players[i];
    }

    public RigidBody add(int i, RigidBody component) {
        rigidBodies[i] = component;
        entities[i] |= RIGID_BODY_BIT;

        for (IComponentListener listener : componentListeners)
            listener.onComponentAdded(i);

        return component;
    }

    public RigidBody getRigidBody(int i) {
        return rigidBodies[i];
    }

    public Terrain add(int i, Terrain component) {
        terrains[i] = component;
        entities[i] |= TERRAIN_BIT;

        for (IComponentListener listener : componentListeners)
            listener.onComponentAdded(i);

        return component;
    }

    public Terrain getTerrain(int i) {
        return terrains[i];
    }

    public TransformComponent add(int i, TransformComponent component) {
        transforms[i] = component;
        entities[i] |= TRANSFORM_BIT;

        for (IComponentListener listener : componentListeners)
            listener.onComponentAdded(i);

        return component;
    }

    public TransformComponent getTransform(int i) {
        return transforms[i];
    }

    public WaterMesh add(int i, WaterMesh component) {
        waterMeshes[i] = component;
        entities[i] |= WATER_MESH_BIT;

        for (IComponentListener listener : componentListeners)
            listener.onComponentAdded(i);

        return component;
    }

    public WaterMesh getWaterMesh(int i) {
        return waterMeshes[i];
    }

    public boolean match(int i, int bits) {
        return (entities[i] & bits) == bits;
    }
}
