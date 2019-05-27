package me.marlon.physics;

import me.marlon.ecs.Terrain;
import me.marlon.gfx.TerrainMesh;
import org.joml.Vector3f;

public class CollisionTerrain {
    private int size; // in tiles
    private Vector3f[] data;

    public CollisionTerrain(Terrain terrain) {
        size = (terrain.getSize() - 1) / TerrainMesh.TILE_SIZE;
    }
}
