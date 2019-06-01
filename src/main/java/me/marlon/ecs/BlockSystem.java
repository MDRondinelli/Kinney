package me.marlon.ecs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockSystem {
    private static final short BITS = EntityManager.BLOCK_BIT;

    private EntityManager entities;

    private int xSize;
    private int ySize;
    private int zSize;
    private Block[] blocks;

    public BlockSystem(EntityManager entities, int xSize, int ySize, int zSize) {
        this.entities = entities;

        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        blocks = new Block[xSize * ySize * zSize];
    }

    public void onUpdate() {
        Arrays.fill(blocks, null);

        List<Block> blockList = new ArrayList<>();

        for (int i = 0; i < EntityManager.MAX_ENTITIES; ++i) {
            if (!entities.match(i, BITS))
                continue;

            Block block = entities.getBlock(i);
            addBlock(block);
            blockList.add(block);
        }

        for (Block block : blockList)
            block.onUpdate();
    }

    private void addBlock(Block block) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        if (x < 0 || x >= xSize || y < 0 || y >= ySize || z < 0 || z >= zSize)
            return;

        blocks[xSize * ySize * z + xSize * y + x] = block;
    }

    public Block getBlock(int x, int y, int z) {
        if (x < 0 || x >= xSize || y < 0 || y >= ySize || z < 0 || z >= zSize)
            return null;

        return blocks[xSize * ySize * z + xSize * y + x];
    }
}
