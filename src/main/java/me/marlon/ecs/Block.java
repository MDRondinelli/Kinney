package me.marlon.ecs;

import me.marlon.game.Item;

public class Block implements IUpdateListener {
    private Item item;
    private boolean functional;
    private int x;
    private int y;
    private int z;

    public Block(Item item, boolean functional, int x, int y, int z) {
        this.item = item;
        this.functional = functional;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void onUpdate() {
    }

    public void onUse(int user) {
    }

    public Item getItem() {
        return item;
    }

    public boolean isFunctional() {
        return functional;
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
