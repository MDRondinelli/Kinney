package me.marlon.gui;

import me.marlon.ecs.Inventory;
import me.marlon.ecs.InventorySlot;
import me.marlon.ecs.Player;
import org.joml.Vector2f;
import org.joml.Vector4f;
import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.List;

public class GuiInventory extends GuiComponent {
    private Inventory inventory;
    private Player player;

    private List<GuiText> texts;
    private GuiComponent color;

    public GuiInventory(GuiOrigin origin, Vector2f position) {
        super(origin, position, new Vector2f(608.0f), new Vector4f(0.0f, 0.0f, 0.0f, 0.9f));
        texts = new ArrayList<>();
    }

    @Override
    public void onKeyPressed(int key) {
        switch (key) {
            case GLFW_KEY_UP: {
                int srcSlotIndex = player.activeSlot.getIndex();
                int dstSlotIndex = srcSlotIndex - 1;

                if (dstSlotIndex == -1)
                    dstSlotIndex = inventory.getNumSlots() - 1;

                player.activeSlot = inventory.getSlot(dstSlotIndex);
                break;
            }
            case GLFW_KEY_DOWN: {
                int srcSlotIndex = player.activeSlot.getIndex();
                int dstSlotIndex = srcSlotIndex + 1;

                if (dstSlotIndex == inventory.getNumSlots())
                    dstSlotIndex = 0;

                player.activeSlot = inventory.getSlot(dstSlotIndex);
                break;
            }
        }
    }

    @Override
    public void draw(GuiManager gui) {
        gui.push(this);
        gui.rect(getSize(), getColor());

        for (int i = 0; i < inventory.getNumSlots(); ++i) {
            InventorySlot slot = inventory.getSlot(i);
            GuiText text = texts.get(i);

            if (slot == player.activeSlot) {
                Vector2f position = new Vector2f(0.0f, getSize().y * 0.5f - i * 32.0f);
                Vector2f size = new Vector2f(getSize().x, 32.0f);
                new GuiComponent(GuiOrigin.TOP, position, size, new Vector4f(0.0f, 0.0f, 0.0f, 0.9f)).draw(gui);
            }

            text.setText(slot.isEmpty() ? "" : slot.getItem().getName() + "-" + slot.getCount());
        }

        for (GuiText text : texts)
            text.draw(gui);

        gui.pop();
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
        texts.clear();

        float xOffs = -0.5f * getSize().x + 10.0f;
        float yOffs = 0.5f * getSize().y;

        for (int i = 0; i < inventory.getNumSlots(); ++i) {
            texts.add(new GuiText(GuiOrigin.TOP_LEFT, new Vector2f(xOffs, yOffs), 32.0f));
            yOffs -= 32.0f;
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
