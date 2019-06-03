package me.marlon.ecs;

import me.marlon.game.Inventory;
import me.marlon.game.InventorySlot;
import org.joml.Vector3f;

public class Player {
    public Inventory inventory;
    public InventorySlot activeSlot;
    public float speed;
    public float reach;
    public Vector3f direction;
    public float angleX;
    public float angleY;
    public boolean jumping;

    public Player(float speed, float reach) {
        inventory = new Inventory(19);
        activeSlot = inventory.getSlot(0);
        this.speed = speed;
        this.reach = reach;
        direction = new Vector3f();
    }
}
