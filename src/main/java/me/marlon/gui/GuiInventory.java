package me.marlon.gui;

import me.marlon.ecs.Inventory;
import me.marlon.ecs.InventorySlot;
import org.joml.Vector2f;
import org.joml.Vector4f;
import static org.lwjgl.glfw.GLFW.*;

import java.util.List;

public class GuiInventory extends GuiComponent {
    private Inventory inventory;

    public GuiInventory(GuiOrigin origin, Vector2f position) {
        super(origin, position, new Vector2f(608.0f), new Vector4f(0.0f, 0.0f, 0.0f, 0.9f));
    }

    @Override
    public void onKeyPressed(int key) {
        switch (key) {
            case GLFW_KEY_UP:
                inventory.setSelection(inventory.getSelection() - 1);
                break;
            case GLFW_KEY_DOWN:
                inventory.setSelection(inventory.getSelection() + 1);
                break;
        }
    }

    @Override
    public void draw(GuiManager gui) {
        gui.push(this);
        gui.rect(getSize(), getColor());

        float xOffs = -0.5f * getSize().x + 10.0f;
        float yOffs = 0.5f * getSize().y;

        List<InventorySlot> slots = inventory.getSlots();

        for (int i = 0; i < slots.size(); ++i) {
            InventorySlot slot = slots.get(i);

            String text = slot.getItem().getName() + "-" + slot.getCount();
            GuiText guiText = new GuiText(GuiOrigin.TOP_LEFT, new Vector2f(xOffs, yOffs), 32.0f, text);

            if (inventory.getSelection() == i) {
                Vector2f position = new Vector2f(getCenter().x, guiText.getCenter().y);
                Vector2f size = new Vector2f(getSize().x, guiText.getSize().y);
                new GuiComponent(GuiOrigin.MID, position, size, new Vector4f(0.0f, 0.0f, 0.0f, 0.9f)).draw(gui);
            }

            guiText.draw(gui);
            yOffs -= 32;
        }

        gui.pop();
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}
