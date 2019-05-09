package me.marlon.game;

import me.marlon.ecs.*;
import org.joml.Vector2f;

public class World implements IKeyListener, IMouseListener {
    private EntityManager entities;
    private CameraSystem cameraSystem;
    private DirectionalLightSystem dLightSystem;
    private MeshSystem meshSystem;
    private ParticleSystem particleSystem;
    private PlayerSystem playerSystem;
    private TerrainSystem terrainSystem;
    private WaterSystem waterSystem;

    public World(Engine engine) {
        entities = new EntityManager();
        cameraSystem = new CameraSystem(entities, engine.getRenderer());
        dLightSystem = new DirectionalLightSystem(entities, engine.getRenderer());
        meshSystem = new MeshSystem(entities, engine.getRenderer());
        playerSystem = new PlayerSystem(entities, engine.getDeltaTime());
        particleSystem = new ParticleSystem(entities, engine.getDeltaTime());
        terrainSystem = new TerrainSystem(entities, engine.getRenderer());
        waterSystem = new WaterSystem(entities, engine.getRenderer());
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
        particleSystem.onUpdate();
        cameraSystem.onUpdate();
        dLightSystem.onUpdate();
        meshSystem.onUpdate();
        terrainSystem.onUpdate();
        waterSystem.onUpdate();
    }

    public EntityManager getEntities() {
        return entities;
    }
}
