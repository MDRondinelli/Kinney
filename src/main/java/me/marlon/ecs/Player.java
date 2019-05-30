package me.marlon.ecs;

import me.marlon.physics.CollisionBox;
import me.marlon.physics.RigidBody;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Player {
    public Vector3f direction;
    public Vector3f oldMovement;
    public Vector3f newMovement;
    public float speed;
    public float angleX;
    public float angleY;
    public float lerp;
    public boolean jumping;

    public Player(float speed) {
        direction = new Vector3f();
        oldMovement = new Vector3f();
        newMovement = new Vector3f();
        this.speed = speed;
    }
}
