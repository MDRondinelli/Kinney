package me.marlon.game;

import me.marlon.gfx.Renderer;

public class TerrainSystem {
    public static final short BITS = EntityManager.TERRAIN_BIT;

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

            renderer.setTerrain(entities.getTerrain(i).terrain);
        }
    }
}
