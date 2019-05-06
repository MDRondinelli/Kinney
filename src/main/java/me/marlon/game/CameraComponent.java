package me.marlon.game;

public class CameraComponent {
    public float fov;
    public float aspect;
    public float zNear;
    public float zFar;

    public CameraComponent(float fov, float aspect, float zNear, float zFar) {
        this.fov = fov;
        this.aspect = aspect;
        this.zNear = zNear;
        this.zFar = zFar;
    }

    public CameraComponent(float fov, float w, float h, float zNear, float zFar) {
        this(fov, w / h, zNear, zFar);
    }
}
