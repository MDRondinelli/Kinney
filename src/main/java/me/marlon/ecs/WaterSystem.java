package me.marlon.ecs;

import me.marlon.gfx.Renderer;

import java.util.HashSet;
import java.util.Set;

public class WaterSystem implements IComponentListener, IUpdateListener {
    private static final short BITS = EntityManager.TRANSFORM_BIT | EntityManager.WATER_MESH_BIT;

    private EntityManager entities;
    private Renderer renderer;

    private Set<Integer> ids;

    public WaterSystem(EntityManager entities, Renderer renderer) {
        this.entities = entities;
        this.renderer = renderer;
        ids = new HashSet<>();
    }

    @Override
    public void onComponentAdded(int entity) {
        if (entities.match(entity, BITS))
            ids.add(entity);
    }

    @Override
    public void onComponentRemoved(int entity) {
        if (!entities.match(entity, BITS))
            ids.remove(entity);
    }

    @Override
    public void onUpdate() {
        for (int id : ids)
            renderer.setWaterMesh(entities.getWaterMesh(id), entities.getTransform(id).getMatrix());
    }
}
