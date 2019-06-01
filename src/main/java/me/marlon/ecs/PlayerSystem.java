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
import java.util.HashSet;
import java.util.Set;

public class PlayerSystem implements IComponentListener, IKeyListener, IMouseListener {
    private static final short BITS = EntityManager.PLAYER_BIT | EntityManager.RIGID_BODY_BIT | EntityManager.TRANSFORM_BIT;

    private EntityManager entities;
    private PhysicsSystem physics;

    private Mesh ballMesh;
    private Mesh boxMesh;

    private Set<Integer> ids;

    public PlayerSystem(EntityManager entities, PhysicsSystem physics) {
        this.entities = entities;
        this.physics = physics;

        try {
            ballMesh = new Mesh(new Primitive("res/meshes/ball.obj", new Vector3f(1.0f, 0.0f, 0.0f)));
            boxMesh = new Mesh(new Primitive("res/meshes/box.obj", new Vector3f(0.0f, 1.0f, 0.0f)));
        } catch (IOException e) {
            e.printStackTrace();
        }

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
    public void onKeyPressed(int key) {
        for (int id : ids) {
             Player player = entities.getPlayer(id);

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

    @Override
    public void onKeyReleased(int key) {
        for (int id : ids) {
            Player player = entities.getPlayer(id);

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

    @Override
    public void onButtonPressed(int button) {
        for (int id : ids) {
            TransformComponent transform = entities.getTransform(id);

            Vector3f playerPosition = new Vector3f(transform.getPosition());
            Vector3f playerDirection = new Vector3f(0.0f, 0.0f, -1.0f).rotate(transform.getOrientation());

            float t = physics.rayCast(playerPosition, playerDirection) - 0.01f;

            if (t < 3.0f) {
                float x = (float) Math.floor(playerPosition.x + playerDirection.x * t) + 0.5f;
                float y = (float) Math.floor(playerPosition.y + playerDirection.y * t) + 0.5f;
                float z = (float) Math.floor(playerPosition.z + playerDirection.z * t) + 0.5f;

                CollisionBox blockCollider = new CollisionBox(PhysicsMaterial.CONCRETE, new Matrix4f(), new Vector3f(0.5f));
                blockCollider.updateDerivedData(new Matrix4f().translate(x, y, z));

                if (!blockCollider.collideWith(entities.getRigidBody(id).getCollider())) {
                    int block = entities.create();
                    entities.add(block, boxMesh);
                    entities.add(block, new TransformComponent()).translate(new Vector3f(x, y, z));
                    entities.add(block, blockCollider);
                }
            }
        }
    }

    @Override
    public void onButtonReleased(int button) {}

    @Override
    public void onMouseMoved(Vector2f position, Vector2f velocity) {
        for (int id : ids) {
            Player player = entities.getPlayer(id);
            player.angleX += velocity.y * -0.001f;
            player.angleY += velocity.x * -0.001f;

            if (player.angleX < (float) Math.PI * -0.5f)
                player.angleX = (float) Math.PI * -0.5f;
            if (player.angleX > (float) Math.PI * 0.5f)
                player.angleX = (float) Math.PI * 0.5f;
        }
    }

    public void onUpdate() {
        for (int id : ids) {
            Player player = entities.getPlayer(id);
            RigidBody body = entities.getRigidBody(id);
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
                movement.rotateY(player.angleY);
                if (movement.lengthSquared() != 0.0f)
                    movement.normalize(player.speed);

                Vector3f velocity = new Vector3f(body.getVelocity()).lerp(movement, 0.2f);

                body.getVelocity().x = velocity.x;
                body.getVelocity().z = velocity.z;

                if (player.jumping)
                    body.getVelocity().y = 4.75f;
            } else {
                Vector3f movement = new Vector3f(player.direction);
                movement.rotateY(player.angleY);
                if (movement.lengthSquared() != 0.0f)
                    movement.normalize(player.speed);

                Vector3f velocity = new Vector3f(body.getVelocity()).lerp(movement, 0.02f);

                body.getVelocity().x = velocity.x;
                body.getVelocity().z = velocity.z;

            }

            player.jumping = false;

            Quaternionf orientation = new Quaternionf();
            orientation.mul(new Quaternionf(new AxisAngle4f(player.angleY, 0.0f, 1.0f, 0.0f)));
            orientation.mul(new Quaternionf(new AxisAngle4f(player.angleX, 1.0f, 0.0f, 0.0f)));

            entities.getTransform(id).getOrientation().set(orientation);
        }
    }
}
