package me.marlon.game;

import me.marlon.gfx.Renderer;
import org.joml.Matrix4f;

public class CameraSystem {
    public static final short BITS = EntityManager.CAMERA_BIT | EntityManager.TRANSFORM_BIT;

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

            CameraComponent camera = entities.getCamera(i);
            TransformComponent transform = entities.getTransform(i);

            Matrix4f view = transform.getInvMatrix();
            Matrix4f viewInv = transform.getMatrix();
            Matrix4f proj = new Matrix4f().perspective(camera.fov, camera.aspect, camera.zNear, camera.zFar);
            Matrix4f projInv = new Matrix4f(proj).invert();

            renderer.setView(view);
            renderer.setViewInv(viewInv);
            renderer.setProj(proj);
            renderer.setProjInv(projInv);
        }
    }
}
