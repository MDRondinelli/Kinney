package me.marlon.ecs;

import me.marlon.game.Item;

public interface BlockFactory {
    Block create(Item item, int x, int y, int z);
}
