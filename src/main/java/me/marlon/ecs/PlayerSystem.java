package me.marlon.ecs;

import static org.lwjgl.glfw.GLFW.*;

import me.marlon.game.IKeyListener;
import me.marlon.game.IMouseListener;
import me.marlon.gfx.Mesh;
import me.marlon.gfx.Primitive;
import me.marlon.gfx.Window;
import me.marlon.physics.*;
import org.joml.*;

import java.io.IOException;
import java.lang.Math;
import java.util.HashSet;
import java.util.Set;

public class PlayerSystem implements IComponentListener, IKeyListener, IMouseListener, IUpdateListener {
    private static final int BITS = EntityManager.PLAYER_BIT | EntityManager.RIGID_BODY_BIT | EntityManager.TRANSFORM_BIT;

    private EntityManager entities;
    private BlockSystem blocks;
    private PhysicsSystem physics;
    private Window window;

    private Mesh ballMesh;
    private Mesh boxMesh;

    private Set<Integer> ids;

    public PlayerSystem(EntityManager entities, BlockSystem blocks, PhysicsSystem physics, Window window) {
        this.entities = entities;
        this.blocks = blocks;
        this.physics = physics;
        this.window = window;

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
                case GLFW_KEY_TAB:
                    window.setMouseGrabbed(!window.isMouseGrabbed());
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
    public void onButtonPressed(int button, Vector2f position) {
        for (int id : ids) {
            TransformComponent transform = entities.getTransform(id);

            Vector3f playerPosition = new Vector3f(transform.getPosition());
            Vector3f playerDirection = new Vector3f(0.0f, 0.0f, -1.0f).rotate(transform.getOrientation());

            float t = physics.rayCast(playerPosition, playerDirection);

            if (t < 3.0f) {
                if (button == GLFW_MOUSE_BUTTON_LEFT) {
                    t += 0.01f;
                    int x = (int) (playerPosition.x + playerDirection.x * t);
                    int y = (int) (playerPosition.y + playerDirection.y * t);
                    int z = (int) (playerPosition.z + playerDirection.z * t);

                    int blockEnt = blocks.getBlock(x, y, z);
                    if (blockEnt != 0xffffffff) {
                        entities.destroy(blockEnt);
                    }
                }

                if (button == GLFW_MOUSE_BUTTON_RIGHT) {
                    t -= 0.01f;
                    int x = (int) (playerPosition.x + playerDirection.x * t);
                    int y = (int) (playerPosition.y + playerDirection.y * t);
                    int z = (int) (playerPosition.z + playerDirection.z * t);

                    CollisionBox boxCollider = new CollisionBox(PhysicsMaterial.CONCRETE, new Matrix4f(), new Vector3f(0.5f));
                    boxCollider.updateDerivedData(new Matrix4f().translate(x + 0.5f, y + 0.5f, z + 0.5f));

                    if (!boxCollider.collideWith(entities.getRigidBody(id).getCollider())) {
                        int block = entities.create();
                        entities.add(block, boxMesh);
                        entities.add(block, boxCollider);
                        entities.add(block, new Block(x, y, z));
                        entities.add(block, new TransformComponent());
                    }
                }
            }
        }
    }

    @Override
    public void onButtonReleased(int button, Vector2f position) {}

    @Override
    public void onMouseMoved(Vector2f position, Vector2f velocity) {
        if (!window.isMouseGrabbed())
            return;

        for (int id : ids) {
            Player player = entities.getPlayer(id);
            player.angleX += velocity.y * 0.001f;
            player.angleY -= velocity.x * 0.001f;

            if (player.angleX < (float) Math.PI * -0.5f)
                player.angleX = (float) Math.PI * -0.5f;
            if (player.angleX > (float) Math.PI * 0.5f)
                player.angleX = (float) Math.PI * 0.5f;
        }
    }

    @Override
    public void onUpdate() {
        for (int id : ids) {
            Player player = entities.getPlayer(id);
            RigidBody body = entities.getRigidBody(id);
            body.setAwake(true);

            float altitude = Float.MAX_VALUE;
            {
                float t = physics.rayCast(new Vector3f(body.getPosition()).add(0.25f, -0.99f, 0.25f), new Vector3f(0.0f, -1.0f, 0.0f));
                if (altitude > t)
                    altitude = t;
            }
            {
                float t = physics.rayCast(new Vector3f(body.getPosition()).add(0.25f, -0.99f, -0.15f), new Vector3f(0.0f, -1.0f, 0.0f));
                if (altitude > t)
                    altitude = t;
            }
            {
                float t = physics.rayCast(new Vector3f(body.getPosition()).add(-0.25f, -0.99f, 0.25f), new Vector3f(0.0f, -1.0f, 0.0f));
                if (altitude > t)
                    altitude = t;
            }
            {
                float t = physics.rayCast(new Vector3f(body.getPosition()).add(-0.25f, -0.99f, -0.25f), new Vector3f(0.0f, -1.0f, 0.0f));
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
