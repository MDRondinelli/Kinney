package me.marlon.ecs;

import me.marlon.gfx.Terrain;

public class TerrainComponent {
    public Terrain terrain;

    public TerrainComponent(Terrain terrain) {
        this.terrain = terrain;
    }
}