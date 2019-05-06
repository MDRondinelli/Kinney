package me.marlon.game;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class PlayerSystem implements IKeyListener, IMouseListener {
    public static final short BITS = EntityManager.PLAYER_BIT | EntityManager.TRANSFORM_BIT;

    private EntityManager entities;
    private float deltaTime;

    public PlayerSystem(EntityManager entities, float deltaTime) {
        this.entities = entities;
        this.deltaTime = deltaTime;
    }

    public void onKeyPressed(int key) {
        for (int i = 0; i < EntityManager.MAX_ENTITIES; ++i) {
            if (!entities.match(i, BITS))
                continue;

             PlayerComponent player = entities.getPlayer(i);

             boolean update = true;
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
                     player.direction.y += 1.0f;
                     break;
                 case GLFW_KEY_LEFT_SHIFT:
                     player.direction.y -= 1.0f;
                     break;
                 default:
                     update = false;
                     break;
             }

             if (update) {
                 player.oldVelocity.lerp(player.newVelocity, player.lerp);

                 if (player.direction.lengthSquared() == 0.0f)
                     player.newVelocity.zero();
                 else
                     player.direction.normalize(player.speed, player.newVelocity);

                 player.lerp = 0.0f;
             }
        }
    }

    public void onKeyReleased(int key) {
        for (int i = 0; i < EntityManager.MAX_ENTITIES; ++i) {
            if (!entities.match(i, BITS))
                continue;

            PlayerComponent player = entities.getPlayer(i);

            boolean update = true;
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
                case GLFW_KEY_SPACE:
                    player.direction.y -= 1.0f;
                    break;
                case GLFW_KEY_LEFT_SHIFT:
                    player.direction.y += 1.0f;
                    break;
                default:
                    update = false;
                    break;
            }

            if (update) {
                player.oldVelocity.lerp(player.newVelocity, player.lerp);

                if (player.direction.lengthSquared() == 0.0f)
                    player.newVelocity.zero();
                else
                    player.direction.normalize(player.speed, player.newVelocity);

                player.lerp = 0.0f;
            }
        }
    }

    public void onButtonPressed(int button) {}

    public void onButtonReleased(int button) {}

    public void onMouseMoved(Vector2f position, Vector2f velocity) {
        for (int i = 0; i < EntityManager.MAX_ENTITIES; ++i) {
            if (!entities.match(i, BITS))
                continue;

            PlayerComponent player = entities.getPlayer(i);
            player.dAngleX += velocity.y * -0.001f;
            player.dAngleY += velocity.x * -0.001f;
        }
    }

    public void onUpdate() {
        for (int i = 0; i < EntityManager.MAX_ENTITIES; ++i) {
            if (!entities.match(i, BITS))
                continue;

            PlayerComponent player = entities.getPlayer(i);
            TransformComponent transform = entities.getTransform(i);

            player.lerp += 4.0f * deltaTime;
            player.lerp = Math.min(player.lerp, 1.0f);

            Vector3f velocity = player.oldVelocity.lerp(player.newVelocity, player.lerp, new Vector3f());
            transform.translate(velocity.mul(0.0f, deltaTime, 0.0f, new Vector3f()));
            transform.translate(transform.getRotation().transform(velocity.mul(deltaTime, 0.0f, deltaTime)));

            player.angleX += player.dAngleX;
            player.dAngleX = 0.0f;
            player.angleY += player.dAngleY;
            player.dAngleY = 0.0f;

            Quaternionf rotation = new Quaternionf();
            rotation.mul(new Quaternionf(new AxisAngle4f(player.angleY, 0.0f, 1.0f, 0.0f)));
            rotation.mul(new Quaternionf(new AxisAngle4f(player.angleX, 1.0f, 0.0f, 0.0f)));
            transform.setRotation(rotation);
        }
    }
}
