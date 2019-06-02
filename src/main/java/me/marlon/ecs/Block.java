package me.marlon.ecs;

import me.marlon.game.Item;

public class Block implements IUpdateListener {
    private Item item;
    private int x;
    private int y;
    private int z;

    public Block(Item item, int x, int y, int z) {
        this.item = item;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void onUpdate() {
    }

    public Item getItem() {
        return item;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
