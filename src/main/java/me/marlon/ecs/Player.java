package me.marlon.ecs;

import org.joml.Vector3f;

public class Player {
    public float speed;
    public Vector3f direction;
    public Vector3f oldVelocity;
    public Vector3f newVelocity;
    public float angleX;
    public float dAngleX;
    public float angleY;
    public float dAngleY;
    public float lerp;

    public Player(float speed) {
        this.speed = speed;

        direction = new Vector3f();
        oldVelocity = new Vector3f();
        newVelocity = new Vector3f();
    }
}
