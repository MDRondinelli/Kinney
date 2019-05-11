package me.marlon.ecs;

import me.marlon.gfx.Mesh;
import me.marlon.gfx.MeshInstance;
import me.marlon.gfx.Renderer;
import org.joml.Matrix4f;

public class MeshSystem {
    private static final short BITS = EntityManager.MESH_BIT | EntityManager.TRANSFORM_BIT;

    private EntityManager entities;
    private Renderer renderer;

    public MeshSystem(EntityManager entities, Renderer renderer) {
        this.entities = entities;
        this.renderer = renderer;
    }

    public void onUpdate() {
        for (int i = 0; i < EntityManager.MAX_ENTITIES; ++i) {
            if (!entities.match(i, BITS))
                continue;

            Mesh mesh = entities.getMesh(i);
            TransformComponent transform = entities.getTransform(i);

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
