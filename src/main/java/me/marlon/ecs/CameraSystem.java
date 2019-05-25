package me.marlon.ecs;

import me.marlon.gfx.Renderer;
import org.joml.Matrix4f;

public class CameraSystem {
    private static final short BITS = EntityManager.CAMERA_BIT | EntityManager.TRANSFORM_BIT;

    private EntityManager entities;
    private Renderer renderer;

    public CameraSystem(EntityManager entities, Renderer renderer) {
        this.entities = entities;
        this.renderer = renderer;
    }

    public void onUpdate() {
        for (int i = 0; i < EntityManager.MAX_ENTITIES; ++i) {
            if (!entities.match(i, BITS))
                continue;

            Camera camera = entities.getCamera(i);
            TransformComponent transform = entities.getTransform(i);

            int parent = transform.parent;
            Matrix4f matrix = new Matrix4f(transform.getMatrix());
            while (parent != 0xffffffff && entities.match(parent, EntityManager.TRANSFORM_BIT)) {
                transform = entities.getTransform(parent);
                parent = transform.parent;
                transform.getMatrix().mul(matrix, matrix);
            }

            Matrix4f view = new Matrix4f(matrix).invert();
            Matrix4f viewInv = matrix;

            Matrix4f proj = new Matrix4f().perspective(camera.fov, camera.aspect, camera.zNear, camera.zFar);
            Matrix4f projInv = new Matrix4f(proj).invert();

            renderer.setView(view);
            renderer.setViewInv(viewInv);
            renderer.setProj(proj);
            renderer.setProjInv(projInv);
        }
    }
}
