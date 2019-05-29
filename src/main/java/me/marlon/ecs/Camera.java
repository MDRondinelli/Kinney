package me.marlon.ecs;

import org.joml.Vector3f;

public class Camera {
    public float fov;
    public float aspect;
    public float zNear;
    public float zFar;
    public Vector3f position;

    public Camera(float fov, float aspect, float zNear, float zFar) {
        this.fov = fov;
        this.aspect = aspect;
        this.zNear = zNear;
        this.zFar = zFar;
        this.position = new Vector3f();
    }

    public Camera(float fov, float w, float h, float zNear, float zFar) {
        this(fov, w / h, zNear, zFar);
    }
}
