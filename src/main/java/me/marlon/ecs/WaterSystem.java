package me.marlon.ecs;

import me.marlon.gfx.Renderer;

public class WaterSystem {
    public static final short BITS = EntityManager.WATER_MESH_BIT;

    private EntityManager entities;
    private Renderer renderer;

    public WaterSystem(EntityManager entities, Renderer renderer) {
        this.entities = entities;
        this.renderer = renderer;
    }

    public void onUpdate() {
        for (int i = 0; i < EntityManager.MAX_ENTITIES; ++i) {
            if (!entities.match(i, BITS))
                continue;

            renderer.setWaterMesh(entities.getWaterMesh(i));
        }
    }
}