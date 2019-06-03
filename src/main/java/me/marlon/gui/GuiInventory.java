package me.marlon.gui;

import me.marlon.game.InventorySlot;
import me.marlon.ecs.Player;
import org.joml.Vector2f;
import org.joml.Vector4f;
import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.List;

public class GuiInventory extends GuiComponent {
    private Player player;

    private GuiText title;
    private List<GuiText> texts;

    public GuiInventory(GuiManager manager, GuiOrigin origin, Vector2f position) {
        super(manager, origin, position, new Vector2f(608.0f), new Vector4f(0.0f, 0.0f, 0.0f, 0.9f));
        texts = new ArrayList<>();
        title = new GuiText(manager, GuiOrigin.BOT, new Vector2f(0.0f, 10.0f + getSize().y * 0.5f), 32.0f, "Inventory");
    }

    @Override
    public void onKeyPressed(int key) {
        switch (key) {
            case GLFW_KEY_UP: {
                int srcSlotIndex = player.activeSlot.getIndex();
                int dstSlotIndex = srcSlotIndex - 1;

                if (dstSlotIndex == -1)
                    dstSlotIndex = player.inventory.getNumSlots() - 1;

                player.activeSlot = player.inventory.getSlot(dstSlotIndex);
                break;
            }
            case GLFW_KEY_DOWN: {
                int srcSlotIndex = player.activeSlot.getIndex();
                int dstSlotIndex = srcSlotIndex + 1;

                if (dstSlotIndex == player.inventory.getNumSlots())
                    dstSlotIndex = 0;

                player.activeSlot = player.inventory.getSlot(dstSlotIndex);
                break;
            }
            case GLFW_KEY_TAB:
                getManager().getLayer(1).remove(this);
                break;
        }
    }

    @Override
    public void draw() {
        getManager().push(this);
        getManager().rect(getSize(), getColor());

        title.draw();

        for (int i = 0; i < player.inventory.getNumSlots(); ++i) {
            InventorySlot slot = player.inventory.getSlot(i);
            GuiText text = texts.get(i);

            if (slot == player.activeSlot) {
                Vector2f position = new Vector2f(0.0f, getSize().y * 0.5f - i * 32.0f);
                Vector2f size = new Vector2f(getSize().x, 32.0f);
                new GuiComponent(getManager(), GuiOrigin.TOP, position, size, new Vector4f(0.0f, 0.0f, 0.0f, 0.9f)).draw();
            }

            text.setText(slot.isEmpty() ? "" : slot.getItem().getName() + "-" + slot.getCount());
        }

        for (GuiText text : texts)
            text.draw();

        getManager().pop();
    }

    public void setPlayer(Player player) {
        this.player = player;
        texts.clear();

        float xOffs = -0.5f * getSize().x + 10.0f;
        float yOffs = 0.5f * getSize().y;

        for (int i = 0; i < player.inventory.getNumSlots(); ++i) {
            texts.add(new GuiText(getManager(), GuiOrigin.TOP_LEFT, new Vector2f(xOffs, yOffs), 32.0f));
            yOffs -= 32.0f;
        }
    }
}
