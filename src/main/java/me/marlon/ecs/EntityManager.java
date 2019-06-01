package me.marlon.ecs;

import me.marlon.game.IKeyListener;
import me.marlon.game.IMouseListener;
import me.marlon.gfx.DirectionalLight;
import me.marlon.gfx.Mesh;
import me.marlon.gfx.Renderer;
import me.marlon.gfx.WaterMesh;
import me.marlon.physics.Collider;
import me.marlon.physics.RigidBody;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class EntityManager implements IUpdateListener, IKeyListener, IMouseListener {
    public static final int MAX_ENTITIES = 2048;

    public static final short BLOCK_BIT = 0x0001;
    public static final short CAMERA_BIT = 0x0002;
    public static final short COLLIDER_BIT = 0x0004;
    public static final short DLIGHT_BIT = 0x0008;
    public static final short MESH_BIT = 0x0010;
    public static final short PLAYER_BIT = 0x0020;
    public static final short RIGID_BODY_BIT = 0x0040;
    public static final short TERRAIN_BIT = 0x0080;
    public static final short TRANSFORM_BIT = 0x0100;
    public static final short WATER_MESH_BIT = 0x0200;

    private ArrayList<Integer> freeList;

    private short[] entities;
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

    private List<IKeyListener> keyListeners;
    private List<IMouseListener> mouseListeners;
    private List<IComponentListener> componentListeners;
    private List<IUpdateListener> updateListeners;

    public EntityManager(float dt, Renderer renderer) {
        freeList = new ArrayList<>(MAX_ENTITIES);
        for (int i = 0; i < MAX_ENTITIES; ++i)
            freeList.add(MAX_ENTITIES - 1 - i);

        entities = new short[MAX_ENTITIES];
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

        keyListeners = new ArrayList<>();
        mouseListeners = new ArrayList<>();
        componentListeners = new ArrayList<>();
        updateListeners = new ArrayList<>();

        CameraSystem cameraSystem = new CameraSystem(this, renderer);
        MeshSystem meshSystem = new MeshSystem(this, renderer);
        PhysicsSystem physicsSystem = new PhysicsSystem(this, dt);
        PlayerSystem playerSystem = new PlayerSystem(this, physicsSystem);
        SunSystem sunSystem = new SunSystem(this, renderer);
        TerrainSystem terrainSystem = new TerrainSystem(this, renderer);
        WaterSystem waterSystem = new WaterSystem(this, renderer);

        keyListeners.add(playerSystem);
        mouseListeners.add(playerSystem);

        componentListeners.add(cameraSystem);
        componentListeners.add(meshSystem);
        componentListeners.add(physicsSystem);
        componentListeners.add(playerSystem);
        componentListeners.add(sunSystem);
        componentListeners.add(terrainSystem);
        componentListeners.add(waterSystem);

        updateListeners.add(physicsSystem);
        updateListeners.add(playerSystem);
        updateListeners.add(cameraSystem);
        updateListeners.add(sunSystem);
        updateListeners.add(meshSystem);
        updateListeners.add(terrainSystem);
        updateListeners.add(waterSystem);
    }

    @Override
    public void onKeyPressed(int key) {
        for (IKeyListener listener : keyListeners)
            listener.onKeyPressed(key);
    }

    @Override
    public void onKeyReleased(int key) {
        for (IKeyListener listener : keyListeners)
            listener.onKeyReleased(key);
    }

    @Override
    public void onButtonPressed(int button) {
        for (IMouseListener listener : mouseListeners)
            listener.onButtonPressed(button);
    }

    @Override
    public void onButtonReleased(int button) {
        for (IMouseListener listener : mouseListeners)
            listener.onButtonReleased(button);
    }

    @Override
    public void onMouseMoved(Vector2f position, Vector2f velocity) {
        for (IMouseListener listener : mouseListeners)
            listener.onMouseMoved(position, velocity);
    }

    @Override
    public void onUpdate() {
        for (IUpdateListener listener : updateListeners)
            listener.onUpdate();
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

    public boolean match(int i, short bits) {
        return (entities[i] & bits) == bits;
    }
}
