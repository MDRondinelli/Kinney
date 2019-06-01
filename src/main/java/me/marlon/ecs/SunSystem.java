package me.marlon.ecs;

import me.marlon.gfx.Renderer;

import java.util.HashSet;
import java.util.Set;

public class SunSystem implements IComponentListener, IUpdateListener {
    private static final short BITS = EntityManager.DLIGHT_BIT;

    private EntityManager entities;
    private Renderer renderer;

    private Set<Integer> ids;

    public SunSystem(EntityManager entities, Renderer renderer) {
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
            renderer.setDLight(entities.getDLight(id));
    }
}
