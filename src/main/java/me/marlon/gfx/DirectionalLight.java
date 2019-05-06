package me.marlon.gfx;

import org.joml.Vector3f;

public class DirectionalLight {
    public Vector3f color;
    public Vector3f direction;

    public DirectionalLight(Vector3f color, Vector3f direction) {
        this.color = color;
        this.direction = direction;
    }
}
