package me.marlon.ecs;

import static org.lwjgl.glfw.GLFW.*;

import me.marlon.game.IKeyListener;
import me.marlon.game.IMouseListener;
import me.marlon.game.Inventory;
import me.marlon.game.InventorySlot;
import me.marlon.gfx.Window;
import me.marlon.gui.GuiInventory;
import me.marlon.gui.GuiManager;
import me.marlon.gui.GuiOrigin;
import me.marlon.gui.GuiText;
import me.marlon.physics.*;
import org.joml.*;

import java.lang.Math;
import java.util.HashSet;
import java.util.Set;

public class PlayerSystem implements IComponentListener, IKeyListener, IMouseListener, IUpdateListener {
    private static final int BITS = EntityManager.PLAYER_BIT | EntityManager.RIGID_BODY_BIT | EntityManager.TRANSFORM_BIT;

    private EntityManager entities;
    private BlockSystem blocks;
    private PhysicsSystem physics;
    private Window window;
    private GuiManager gui;
    private GuiInventory guiInventory;
    private GuiText guiHud;

    private Set<Integer> ids;

    public PlayerSystem(EntityManager entities, BlockSystem blocks, PhysicsSystem physics, Window window, GuiManager gui) {
        this.entities = entities;
        this.blocks = blocks;
        this.physics = physics;
        this.window = window;
        this.gui = gui;

        guiInventory = new GuiInventory(gui, GuiOrigin.MID, new Vector2f(0.0f));
        guiHud = new GuiText(gui, GuiOrigin.BOT, new Vector2f(0.0f, 10.0f - gui.getHeight() * 0.5f), 32.0f);
        gui.getLayer(0).add(guiHud);

        ids = new HashSet<>();
    }

    @Override
    public void onComponentAdded(int entity) {
        if (entities.match(entity, BITS)) {
            ids.add(entity);
            guiInventory.setPlayer(entities.getPlayer(entity));
        }
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
                 case GLFW_KEY_E: {
                     TransformComponent transform = entities.getTransform(id);

                     Vector3f o = new Vector3f(transform.getPosition());
                     Vector3f d = new Vector3f(0.0f, 0.0f, -1.0f).rotate(transform.getOrientation());

                     Intersection isect = physics.rayCast(o, d);
                     if (isect != null && isect.getT() < player.reach) {
                         int x = (int) (isect.getPosition().x - isect.getNormal().x * 0.1f);
                         int y = (int) (isect.getPosition().y - isect.getNormal().y * 0.1f);
                         int z = (int) (isect.getPosition().z - isect.getNormal().z * 0.1f);

                         int blockEnt = blocks.getBlock(x, y, z);
                         if (blockEnt != 0xffffffff) {
                             Block block = entities.getBlock(blockEnt);
                             block.onUse(id);
                         }
                     }

                     break;
                 }
                 case GLFW_KEY_TAB:
                     gui.getLayer(1).toggle(guiInventory);
                     break;
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
            }
        }
    }

    @Override
    public void onButtonPressed(int button, Vector2f position) {
//        if (!window.isMouseGrabbed())
//            return;

        for (int id : ids) {
            Player player = entities.getPlayer(id);

            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                TransformComponent transform = entities.getTransform(id);

                Vector3f o = new Vector3f(transform.getPosition());
                Vector3f d = new Vector3f(0.0f, 0.0f, -1.0f).rotate(transform.getOrientation());

                Intersection isect = physics.rayCast(o, d);
                if (isect != null && isect.getT() < player.reach) {
                    int x = (int) (isect.getPosition().x - isect.getNormal().x * 0.1f);
                    int y = (int) (isect.getPosition().y - isect.getNormal().y * 0.1f);
                    int z = (int) (isect.getPosition().z - isect.getNormal().z * 0.1f);

                    int above = blocks.getBlock(x, y + 1, z);
                    if (above != 0xffffffff && entities.getBlock(above).isFunctional())
                        return;

                    int blockEnt = blocks.getBlock(x, y, z);
                    if (blockEnt != 0xffffffff) {
                        Block block = entities.getBlock(blockEnt);
                        player.inventory.add(block.getItem(), 1);
                        entities.destroy(blockEnt);
                    }
                }
            } else if (button == GLFW_MOUSE_BUTTON_RIGHT) {
                InventorySlot slot = player.activeSlot;
                if (!slot.isEmpty() && slot.getItem().use(id))
                    slot.remove(1);
            }
        }
    }

    @Override
    public void onButtonReleased(int button, Vector2f position) {}

    @Override
    public void onMouseMoved(Vector2f position, Vector2f velocity) {
//        if (!window.isMouseGrabbed())
//            return;

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
            InventorySlot slot = player.activeSlot;
            guiHud.setText(slot.isEmpty() ? "" : slot.getItem().getName() + "-" + slot.getCount());

            RigidBody body = entities.getRigidBody(id);
            body.setAwake(true);

            float altitude = Float.MAX_VALUE;
            {
                Intersection isect = physics.rayCast(new Vector3f(body.getPosition()).add(0.25f, -0.99f, 0.25f), new Vector3f(0.0f, -1.0f, 0.0f));
                if (isect != null && isect.getT() < altitude)
                    altitude = isect.getT();
            }
            {
                Intersection isect = physics.rayCast(new Vector3f(body.getPosition()).add(0.25f, -0.99f, -0.15f), new Vector3f(0.0f, -1.0f, 0.0f));
                if (isect != null && isect.getT() < altitude)
                    altitude = isect.getT();
            }
            {
                Intersection isect = physics.rayCast(new Vector3f(body.getPosition()).add(-0.25f, -0.99f, 0.25f), new Vector3f(0.0f, -1.0f, 0.0f));
                if (isect != null && isect.getT() < altitude)
                    altitude = isect.getT();
            }
            {
                Intersection isect = physics.rayCast(new Vector3f(body.getPosition()).add(-0.25f, -0.99f, -0.25f), new Vector3f(0.0f, -1.0f, 0.0f));
                if (isect != null && isect.getT() < altitude)
                    altitude = isect.getT();
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
