package me.marlon.game;

import org.joml.Vector3f;

public class PlayerComponent {
    public float speed;
    public Vector3f direction;
    public Vector3f oldVelocity;
    public Vector3f newVelocity;
    public float angleX;
    public float dAngleX;
    public float angleY;
    public float dAngleY;
    public float lerp;

    public PlayerComponent(float speed) {
        this.speed = speed;

        direction = new Vector3f();
        oldVelocity = new Vector3f();
        newVelocity = new Vector3f();
    }
}
