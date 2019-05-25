package me.marlon.ecs;

import me.marlon.gfx.Renderer;

public class TerrainSystem {
    private static final short BITS = EntityManager.TERRAIN_BIT;

    private EntityManager entities;
    private Renderer renderer;

    public TerrainSystem(EntityManager entities, Renderer renderer) {
        this.entities = entities;
        this.renderer = renderer;
    }

    public void onUpdate() {
        for (int i = 0; i < EntityManager.MAX_ENTITIES; ++i) {
            if (!entities.match(i, BITS))
                continue;

            renderer.setTerrainMesh(entities.getTerrain(i).getMesh());
        }
    }
}
