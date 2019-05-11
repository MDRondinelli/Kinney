package me.marlon.gfx;

import me.marlon.ecs.Terrain;

import java.util.ArrayList;
import java.util.List;

public class TerrainMesh implements AutoCloseable {
    public static final int CHUNK_SIZE = 8;

    private List<TerrainChunk> chunks;

    public TerrainMesh(Terrain terrain) {
        chunks = new ArrayList<>();

        for (int i = 0; i < terrain.getSizeTiles() / CHUNK_SIZE; ++i)
            for (int j = 0; j < terrain.getSizeTiles() / CHUNK_SIZE; ++j)
                chunks.add(new TerrainChunk(terrain, i * CHUNK_SIZE, j * CHUNK_SIZE, i * CHUNK_SIZE + CHUNK_SIZE, j * CHUNK_SIZE + CHUNK_SIZE));
    }

    public void close() {
        for (int i = 0; i < chunks.size(); ++i)
            chunks.get(i).close();
    }

    public void draw() {
        for (int i = 0; i < chunks.size(); ++i)
            chunks.get(i).draw();
    }

    public List<TerrainChunk> getChunks() {
        return chunks;
    }
}
