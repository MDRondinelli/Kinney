package me.marlon.ecs;

import me.marlon.game.ErosionParticle;
import me.marlon.gfx.TerrainMesh;
import me.marlon.util.OpenSimplexOctaves;
import org.joml.Vector2f;

public class Terrain implements AutoCloseable {
    private int size;
    private float[][] heightmap;

    private TerrainMesh mesh;

    public Terrain(int size) {
        this.size = size;
        heightmap = new float[size][size];

        OpenSimplexOctaves noise = new OpenSimplexOctaves(48.0f, 0.5f, 2.0f,  16);

        float minHeight = Float.MAX_VALUE;
        float maxHeight = Float.MIN_VALUE;
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                heightmap[i][j] = noise.eval(i, j);
                minHeight = Math.min(minHeight, heightmap[i][j]);
                maxHeight = Math.max(maxHeight, heightmap[i][j]);
            }
        }

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                heightmap[i][j] = (heightmap[i][j] - minHeight) / (maxHeight - minHeight);
                float distance = Vector2f.distance(size * 0.5f, size * 0.5f, i, j);
                distance *= 3.0 / size;

                float mask = 1.0f - (float) Math.pow(Math.min(distance, 1.0f), 2.0f);
                heightmap[i][j] *= mask;

                if (distance > 1.0f)
                    heightmap[i][j] -= (distance - 1.0f) * 0.5f;
            }
        }

        for (int i = 0; i < size * size * 8; ++i) {
            ErosionParticle particle = new ErosionParticle(new Vector2f((float) Math.random() * size, (float) Math.random() * size), 1.0f);
            for (int j = 0; j < 32; ++j)
                particle.update(this);
        }

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                heightmap[i][j] *= 48.0f;
                heightmap[i][j] += Math.random() * 0.25f - 0.125f;
            }
        }

        mesh = new TerrainMesh(this);
    }

    public void close() {
        mesh.close();
    }


    public float sample(int x, int y) {
        return heightmap[x][y];
    }

    public float sample(float x, float y) {
        int x0 = Math.min(Math.max((int) x, 0), size - 1);
        int x1 = Math.min(Math.max((int) (x + 1.0f), 0), size - 1);
        int y0 = Math.min(Math.max((int) y, 0), size - 1);
        int y1 = Math.min(Math.max((int) (y + 1.0f), 0), size - 1);

        float u = Math.min(x - x0, 1.0f);
        float v = Math.min(y - y0, 1.0f);

        float l = (1.0f - v) * heightmap[x0][y0] + v * heightmap[x0][y1];
        float r = (1.0f - v) * heightmap[x1][y0] + v * heightmap[x1][y1];
        return (1.0f - u) * l + u * r;
    }

    public Vector2f gradient(float x, float y) {
        int x0 = Math.min(Math.max((int) Math.floor(x), 0), size - 1);
        int x1 = Math.min(Math.max((int) Math.ceil(x), 0), size - 1);
        int y0 = Math.min(Math.max((int) Math.floor(y), 0), size - 1);
        int y1 = Math.min(Math.max((int) Math.ceil(y), 0), size - 1);

        float u = Math.min(x - x0, 1.0f);
        float v = Math.min(x - x0, 1.0f);

        Vector2f g = new Vector2f();
        g.x = (heightmap[x1][y0] - heightmap[x0][y0]) * (1.0f - v) + (heightmap[x1][y1] - heightmap[x0][y1]) * v;
        g.y = (heightmap[x0][y1] - heightmap[x0][y0]) * (1.0f - u) + (heightmap[x1][y1] - heightmap[x1][y0]) * u;
        return g;
    }

    public void deposit(float x, float y, float amt) {
        if (x < 0.0f || x >= size - 1 || y < 0.0f || y >= size - 1)
            return;

        float u = x - (int) x;
        float v = y - (int) y;

        heightmap[(int) x][(int) y] += amt * (1.0f - u) * (1.0f - v);
        heightmap[(int) x][(int) y + 1] += amt * (1.0f - u) * v;

        heightmap[(int) x + 1][(int) y] += amt * u * (1.0f - v);
        heightmap[(int) x + 1][(int) y + 1] += amt * u * v;
    }

    public void erode(float x, float y, float r, float amt) {
        float normalizationFactor = 0.0f;

        for (int i = (int) (x - r); i <= (int) (x + r); ++i) {
            if (i < 0 || i >= size)
                continue;

            for (int j = (int) (y - r); j <= (int) (y + r); ++j) {
                if (j < 0 || j >= size)
                    continue;

                normalizationFactor += Math.max(0.0f, r - Vector2f.distance(i, j, x, y));
            }
        }

        for (int i = (int) (x - r); i <= (int) (x + r); ++i) {
            if (i < 0 || i >= size)
                continue;

            for (int j = (int) (y - r); j <= (int) (y + r); ++j) {
                if (j < 0 || j >= size)
                    continue;

                float w = Math.max(0.0f, r - Vector2f.distance(i, j, x, y));
                heightmap[i][j] -= amt * w / normalizationFactor;
            }
        }
    }

    public int getSize() {
        return size;
    }

    public TerrainMesh getMesh() {
        return mesh;
    }
}
