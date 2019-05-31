package me.marlon.ecs;

import org.joml.Vector3f;

public class Player {
    public Vector3f direction;
    public float speed;
    public float angleX;
    public float angleY;
    public boolean jumping;

    public Player(float speed) {
        direction = new Vector3f();
        this.speed = speed;
    }
}
