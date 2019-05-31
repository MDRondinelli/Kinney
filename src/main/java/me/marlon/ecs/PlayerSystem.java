package me.marlon.ecs;

import static org.lwjgl.glfw.GLFW.*;

import me.marlon.game.IKeyListener;
import me.marlon.game.IMouseListener;
import me.marlon.gfx.Mesh;
import me.marlon.gfx.Primitive;
import me.marlon.physics.*;
import org.joml.*;

import java.io.IOException;
import java.lang.Math;

public class PlayerSystem implements IKeyListener, IMouseListener {
    private static final short BITS = EntityManager.PLAYER_BIT | EntityManager.RIGID_BODY_BIT | EntityManager.TRANSFORM_BIT;

    private EntityManager entities;
    private PhysicsSystem physics;
    private float deltaTime;

    private Mesh ballMesh;
    private Mesh boxMesh;

    public PlayerSystem(EntityManager entities, PhysicsSystem physics, float deltaTime) {
        this.entities = entities;
        this.physics = physics;
        this.deltaTime = deltaTime;

        try {
            ballMesh = new Mesh(new Primitive("res/meshes/ball.obj", new Vector3f(1.0f, 0.0f, 0.0f)));
            boxMesh = new Mesh(new Primitive("res/meshes/box.obj", new Vector3f(0.0f, 1.0f, 0.0f)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onKeyPressed(int key) {
        for (int i = 0; i < EntityManager.MAX_ENTITIES; ++i) {
            if (!entities.match(i, BITS))
                continue;

             Player player = entities.getPlayer(i);

             switch (key) {
                 case GLFW_KEY_A:
                     player.direction.x -= 1.0f;
                     break;
                 case GLFW_KEY_D:
                     player.direction.x += 1.0f;
                     break;
                 case GLFW_KEY_W:
                     player.direction.z -= 1.0f;
                     break;
                 case GLFW_KEY_S:
                     player.direction.z += 1.0f;
                     break;
                 case GLFW_KEY_SPACE:
                     player.jumping = true;
                     break;
//                 case GLFW_KEY_LEFT_SHIFT:
//                     player.speed += 4.0f;
//                     break;
             }
        }
    }

    public void onKeyReleased(int key) {
        for (int i = 0; i < EntityManager.MAX_ENTITIES; ++i) {
            if (!entities.match(i, BITS))
                continue;

            Player player = entities.getPlayer(i);

            switch (key) {
                case GLFW_KEY_A:
                    player.direction.x += 1.0f;
                    break;
                case GLFW_KEY_D:
                    player.direction.x -= 1.0f;
                    break;
                case GLFW_KEY_W:
                    player.direction.z += 1.0f;
                    break;
                case GLFW_KEY_S:
                    player.direction.z -= 1.0f;
                    break;
//                case GLFW_KEY_LEFT_SHIFT:
//                    player.speed -= 4.0f;
//                    break;
            }
        }
    }

    public void onButtonPressed(int button) {
        for (int i = 0; i < EntityManager.MAX_ENTITIES; ++i) {
            if (!entities.match(i, BITS))
                continue;

            TransformComponent transform = entities.getTransform(i);

            Vector3f playerPosition = new Vector3f(transform.getPosition());
            Vector3f playerDirection = new Vector3f(0.0f, 0.0f, -1.0f).rotate(transform.getRotation());

            float t = physics.rayCast(playerPosition, playerDirection) - 0.01f;

            if (t < 3.0f) {
                float x = (float) Math.floor(playerPosition.x + playerDirection.x * t) + 0.5f;
                float y = (float) Math.floor(playerPosition.y + playerDirection.y * t) + 0.5f;
                float z = (float) Math.floor(playerPosition.z + playerDirection.z * t) + 0.5f;

                RigidBody blockBody = RigidBody.createCuboid(PhysicsMaterial.CONCRETE, new Vector3f(0.5f), 0.0f, new Vector3f(x, y, z));
                if (!blockBody.getCollider().collideWith(entities.getRigidBody(i).getCollider())) {
                    int block = entities.create();
                    entities.add(block, boxMesh);
                    entities.add(block, new TransformComponent()).scale(0.5f);
                    entities.add(block, blockBody);
                }
            }
        }
    }

    public void onButtonReleased(int button) {}

    public void onMouseMoved(Vector2f position, Vector2f velocity) {
        for (int i = 0; i < EntityManager.MAX_ENTITIES; ++i) {
            if (!entities.match(i, BITS))
                continue;

            Player player = entities.getPlayer(i);
            player.angleX += velocity.y * -0.001f;
            player.angleY += velocity.x * -0.001f;

            if (player.angleX < (float) Math.PI * -0.5f)
                player.angleX = (float) Math.PI * -0.5f;
            if (player.angleX > (float) Math.PI * 0.5f)
                player.angleX = (float) Math.PI * 0.5f;
        }
    }

    public void onUpdate() {
        for (int i = 0; i < EntityManager.MAX_ENTITIES; ++i) {
            if (!entities.match(i, BITS))
                continue;

            Player player = entities.getPlayer(i);
            RigidBody body = entities.getRigidBody(i);
            body.setAwake(true);

            float altitude = Float.MAX_VALUE;
            {
                float t = physics.rayCast(new Vector3f(body.getPosition()).add(0.1f, -0.99f, 0.1f), new Vector3f(0.0f, -1.0f, 0.0f));
                if (altitude > t)
                    altitude = t;
            }
            {
                float t = physics.rayCast(new Vector3f(body.getPosition()).add(0.1f, -0.99f, -0.1f), new Vector3f(0.0f, -1.0f, 0.0f));
                if (altitude > t)
                    altitude = t;
            }
            {
                float t = physics.rayCast(new Vector3f(body.getPosition()).add(-0.1f, -0.99f, 0.1f), new Vector3f(0.0f, -1.0f, 0.0f));
                if (altitude > t)
                    altitude = t;
            }
            {
                float t = physics.rayCast(new Vector3f(body.getPosition()).add(-0.1f, -0.99f, -0.1f), new Vector3f(0.0f, -1.0f, 0.0f));
                if (altitude > t)
                    altitude = t;
            }

            if (altitude < 0.15f) {
                Vector3f movement = new Vector3f(player.direction);
                if (movement.lengthSquared() != 0.0f) {
                    movement.normalize(player.speed);
                    movement.rotateY(player.angleY);
                }

                Vector3f velocity = new Vector3f(body.getVelocity()).lerp(movement, 0.2f);

                body.getVelocity().x = velocity.x;
                body.getVelocity().z = velocity.z;

                if (player.jumping)
                    body.getVelocity().y = 4.75f;
            } else {
                Vector3f movement = new Vector3f(player.direction);
                if (movement.lengthSquared() != 0.0f) {
                    movement.normalize(player.speed);
                    movement.rotateY(player.angleY);
                }

                Vector3f velocity = new Vector3f(body.getVelocity()).lerp(movement, 0.02f);

                body.getVelocity().x = velocity.x;
                body.getVelocity().z = velocity.z;

            }

            player.jumping = false;

            Quaternionf orientation = new Quaternionf();
            orientation.mul(new Quaternionf(new AxisAngle4f(player.angleY, 0.0f, 1.0f, 0.0f)));
            orientation.mul(new Quaternionf(new AxisAngle4f(player.angleX, 1.0f, 0.0f, 0.0f)));

            entities.getTransform(i).getRotation().set(orientation);
        }
    }
}
