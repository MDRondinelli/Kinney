package me.marlon.ecs;

public class Block implements IUpdateListener {
    private int x;
    private int y;
    private int z;

    public Block(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void onUpdate() {
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
