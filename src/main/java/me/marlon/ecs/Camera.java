package me.marlon.ecs;

public class Camera {
    public float fov;
    public float aspect;
    public float zNear;
    public float zFar;

    public Camera(float fov, float aspect, float zNear, float zFar) {
        this.fov = fov;
        this.aspect = aspect;
        this.zNear = zNear;
        this.zFar = zFar;
    }

    public Camera(float fov, float w, float h, float zNear, float zFar) {
        this(fov, w / h, zNear, zFar);
    }
}
