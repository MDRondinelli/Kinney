package me.marlon.ecs;

import org.joml.Vector3f;

public class Player {
    public InventorySlot activeSlot;
    public float speed;
    public float reach;
    public Vector3f direction;
    public float angleX;
    public float angleY;
    public boolean jumping;

    public Player(InventorySlot activeSlot, float speed, float reach) {
        System.out.println(activeSlot);
        this.activeSlot = activeSlot;
        this.speed = speed;
        this.reach = reach;
        direction = new Vector3f();
    }
}
