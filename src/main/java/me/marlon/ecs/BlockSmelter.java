package me.marlon.ecs;

import me.marlon.game.Inventory;
import me.marlon.game.Item;

public class BlockSmelter extends Block {
    public static final int INPUT_SLOT = 0;
    public static final int FUEL_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;

    private Inventory inventory;
    private float energy;

    public BlockSmelter(Item item, int x, int y, int z) {
        super(item, true, x, y, z);
        inventory = new Inventory(3);
    }

    @Override
    public void onUse(int user) {

    }
}
