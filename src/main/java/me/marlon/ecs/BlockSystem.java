package me.marlon.ecs;

import java.util.*;

public class BlockSystem implements IComponentListener, IUpdateListener {
    private static final int BITS = EntityManager.BLOCK_BIT | EntityManager.TRANSFORM_BIT;

    private EntityManager entities;

    private int xSize;
    private int ySize;
    private int zSize;
    private int[] blocks;

    private Set<Integer> ids;

    public BlockSystem(EntityManager entities, int xSize, int ySize, int zSize) {
        this.entities = entities;

        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        blocks = new int[xSize * ySize * zSize];
        ids = new HashSet<>();

        Arrays.fill(blocks, 0xffffffff);
    }

    @Override
    public void onComponentAdded(int entity) {
        if (entities.match(entity, BITS)) {
            Block block = entities.getBlock(entity);
            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();
            int idx = xSize * ySize * z + xSize * y + x;
            blocks[idx] = entity;
            ids.add(entity);
        }
    }

    @Override
    public void onComponentRemoved(int entity) {
        if (!entities.match(entity, BITS))
            ids.remove(entity);
    }

    public int getBlock(int x, int y, int z) {
        int idx = xSize * ySize * z + xSize * y + x;

        if (blocks[idx] != 0xffffffff && !entities.match(blocks[idx], BITS))
            blocks[idx] = 0xffffffff;

        return blocks[idx];
    }

    @Override
    public void onUpdate() {
        for (int id : ids) {
            Block block = entities.getBlock(id);
            TransformComponent transform = entities.getTransform(id);

            block.onUpdate();
            transform.getPosition().x = block.getX() + 0.5f;
            transform.getPosition().y = block.getY() + 0.5f;
            transform.getPosition().z = block.getZ() + 0.5f;
            transform.getOrientation().set(0.0f, 0.0f, 0.0f, 1.0f);
            transform.setScale(1.0f);
        }
    }
}
