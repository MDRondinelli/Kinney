package me.marlon.ecs;

import me.marlon.gfx.Mesh;
import me.marlon.gfx.MeshInstance;
import me.marlon.gfx.Renderer;
import org.joml.Matrix4f;

import java.util.HashSet;
import java.util.Set;

public class MeshSystem implements IComponentListener {
    private static final short BITS = EntityManager.MESH_BIT | EntityManager.TRANSFORM_BIT;

    private EntityManager entities;
    private Renderer renderer;

    private Set<Integer> ids;

    public MeshSystem(EntityManager entities, Renderer renderer) {
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

    public void onUpdate() {
        for (int id : ids) {
            Mesh mesh = entities.getMesh(id);
            TransformComponent transform = entities.getTransform(id);

            int parent = transform.parent;
            Matrix4f matrix = new Matrix4f(transform.getMatrix());

            while (parent != 0xffffffff && entities.match(parent, EntityManager.TRANSFORM_BIT)) {
                transform = entities.getTransform(parent);
                parent = transform.parent;
                transform.getMatrix().mul(matrix, matrix);
            }

            renderer.draw(new MeshInstance(mesh, matrix));
        }
    }
}
