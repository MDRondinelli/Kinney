package me.marlon.game;

import org.joml.Vector2f;

public class World implements IKeyListener, IMouseListener {
    EntityManager entities;
    CameraSystem cameraSystem;
    MeshSystem meshSystem;
    PlayerSystem playerSystem;
    TerrainSystem terrainSystem;

    public World(Engine engine) {
        entities = new EntityManager();
        cameraSystem = new CameraSystem(entities, engine.getRenderer());
        meshSystem = new MeshSystem(entities, engine.getRenderer());
        playerSystem = new PlayerSystem(entities, engine.getDeltaTime());
        terrainSystem = new TerrainSystem(entities, engine.getRenderer());
    }

    public void onKeyPressed(int key) {
        playerSystem.onKeyPressed(key);
    }

    public void onKeyReleased(int key) {
        playerSystem.onKeyReleased(key);
    }

    public void onButtonPressed(int button) {
        playerSystem.onButtonPressed(button);
    }

    public void onButtonReleased(int button) {
        playerSystem.onButtonReleased(button);
    }

    public void onMouseMoved(Vector2f position, Vector2f velocity) {
        playerSystem.onMouseMoved(position, velocity);
    }

    public void onUpdate() {
        playerSystem.onUpdate();
        cameraSystem.onUpdate();
        meshSystem.onUpdate();
        terrainSystem.onUpdate();
    }

    public EntityManager getEntities() {
        return entities;
    }

    public CameraSystem getCameraSystem() {
        return cameraSystem;
    }

    public MeshSystem getMeshSystem() {
        return meshSystem;
    }
}
