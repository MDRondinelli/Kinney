package me.marlon.physics;

import org.joml.Vector3f;

public class Intersection {
    private float t;
    private Vector3f position;
    private Vector3f normal;

    public Intersection(float t, Vector3f position, Vector3f normal) {
        this.t = t;
        this.position = position;
        this.normal = normal;
    }

    public float getT() {
        return t;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getNormal() {
        return normal;
    }
}
