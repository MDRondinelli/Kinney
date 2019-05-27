package me.marlon.physics;

import me.marlon.ecs.Terrain;
import me.marlon.gfx.TerrainMesh;
import org.joml.AABBf;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public class CollisionTerrain extends CollisionPrimitive {
    private int size; // in tiles
    private Vector3f[] data; // every 3 vec3s is a tri - every 2 tris is a quad - every size quads is a column

    public CollisionTerrain(Terrain terrain) {
        super(new Matrix4f(), new AABBf(0.0f, -Float.MAX_VALUE, 0.0f, terrain.getSize(), Float.MAX_VALUE, terrain.getSize()));
        size = (terrain.getSize() - 1) / TerrainMesh.TILE_SIZE;
        data = new Vector3f[size * size * 6];

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                float h0 = terrain.sample(i * TerrainMesh.TILE_SIZE, j * TerrainMesh.TILE_SIZE);
                float h1 = terrain.sample(i * TerrainMesh.TILE_SIZE, (j + 1) * TerrainMesh.TILE_SIZE);
                float h2 = terrain.sample((i + 1) * TerrainMesh.TILE_SIZE, j * TerrainMesh.TILE_SIZE);
                float h3 = terrain.sample((i + 1) * TerrainMesh.TILE_SIZE, (j + 1) * TerrainMesh.TILE_SIZE);

                int quadOffs = (size * i + j) * 6;

                float h = (h0 + h1 + h2 + h3) * 0.25f;
                if (Math.abs((h1 + h2) * 0.5f - h) <= Math.abs((h0 + h3) * 0.5 - h)) {
                    // 0, 1, 2
                    data[quadOffs] = new Vector3f(i * TerrainMesh.TILE_SIZE, h0, j * TerrainMesh.TILE_SIZE);
                    data[quadOffs + 1] = new Vector3f(i * TerrainMesh.TILE_SIZE, h1, (j + 1) * TerrainMesh.TILE_SIZE);
                    data[quadOffs + 2] = new Vector3f((i + 1) * TerrainMesh.TILE_SIZE, h2, j * TerrainMesh.TILE_SIZE);

                    // 3, 2, 1
                    data[quadOffs + 3] = new Vector3f((i + 1) * TerrainMesh.TILE_SIZE, h3, (j + 1) * TerrainMesh.TILE_SIZE);
                    data[quadOffs + 4] = new Vector3f((i + 1) * TerrainMesh.TILE_SIZE, h2, j * TerrainMesh.TILE_SIZE);
                    data[quadOffs + 5] = new Vector3f(i * TerrainMesh.TILE_SIZE, h1, (j + 1) * TerrainMesh.TILE_SIZE);
                } else {
                    // 1, 3, 0
                    data[quadOffs] = new Vector3f(i * TerrainMesh.TILE_SIZE, h1, (j + 1) * TerrainMesh.TILE_SIZE);
                    data[quadOffs + 1] = new Vector3f((i + 1) * TerrainMesh.TILE_SIZE, h3, (j + 1) * TerrainMesh.TILE_SIZE);
                    data[quadOffs + 2] = new Vector3f(i * TerrainMesh.TILE_SIZE, h0, j * TerrainMesh.TILE_SIZE);

                    // 2, 0, 3
                    data[quadOffs + 3] = new Vector3f((i + 1) * TerrainMesh.TILE_SIZE, h2, j * TerrainMesh.TILE_SIZE);
                    data[quadOffs + 4] = new Vector3f(i * TerrainMesh.TILE_SIZE, h0, j * TerrainMesh.TILE_SIZE);
                    data[quadOffs + 5] = new Vector3f((i + 1) * TerrainMesh.TILE_SIZE, h3, (j + 1) * TerrainMesh.TILE_SIZE);
                }
            }
        }
    }

    protected void collideWith(CollisionSphere other, List<Contact> contacts) {
        CollisionDetector.collide(this, other, contacts);
    }

    protected void collideWith(CollisionBox other, List<Contact> contacts) {
        CollisionDetector.collide(this, other, contacts);
    }

    public void getTriangle(int x, int z, Vector3f[] triangle, int which) {
        int offs = (size * x + z) * 6 + which * 3;
        triangle[0] = data[offs];
        triangle[1] = data[offs + 1];
        triangle[2] = data[offs + 2];
    }

    public int getSize() {
        return size;
    }
}
