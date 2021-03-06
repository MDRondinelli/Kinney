package me.marlon.util;

public class OpenSimplexOctaves {
    private float scale;
    private float persistence;
    private float lacunarity;
    private OpenSimplexNoise[] octaves;

    public OpenSimplexOctaves(float scale, float persistence, float lacunarity, int count) {
        this.scale = scale;
        this.persistence = persistence;
        this.lacunarity = lacunarity;

        octaves = new OpenSimplexNoise[count];

        for (int i = 0; i < octaves.length; ++i)
            octaves[i] = new OpenSimplexNoise(System.currentTimeMillis());
    }

    public float eval(float x, float y) {
        float amplitude = 1;
        float frequency = 1;

        float ret = 0.0f;

        for (int i = 0; i < octaves.length; ++i) {
            ret += (float) octaves[i].eval(x / scale * frequency, y / scale * frequency) * amplitude;

            amplitude *= persistence;
            frequency *= lacunarity;
        }

        return ret;
    }
}
