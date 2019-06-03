package me.marlon.gui;

import me.marlon.game.Inventory;
import me.marlon.game.InventorySlot;
import me.marlon.game.Item;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class GuiChest extends GuiComponent {
    private Inventory leftInventory;
    private GuiComponent leftPane;
    private GuiText leftTitle;
    private List<GuiText> leftTexts;

    private Inventory rightInventory;
    private GuiComponent rightPane;
    private GuiText rightTitle;
    private List<GuiText> rightTexts;

    private int index;
    private boolean side;

    public GuiChest(GuiManager manager, GuiOrigin origin, Vector2f position) {
        super(manager, origin, position, new Vector2f(1280.0f, 720.0f), new Vector4f(0.0f));

        leftPane = new GuiComponent(manager, GuiOrigin.MID_RIGHT, new Vector2f(-10.0f, 0.0f), new Vector2f(608.0f), new Vector4f(0.0f, 0.0f, 0.0f, 0.9f));
        leftTitle = new GuiText(manager, GuiOrigin.BOT, new Vector2f(leftPane.getCenter().x, leftPane.getCenter().y + leftPane.getSize().y * 0.5f + 10.0f), 32.0f, "Player");
        leftTexts = new ArrayList<>();

        rightPane = new GuiComponent(manager, GuiOrigin.MID_LEFT, new Vector2f(10.0f, 0.0f), new Vector2f(608.0f), new Vector4f(0.0f, 0.0f, 0.0f, 0.9f));
        rightTitle = new GuiText(manager, GuiOrigin.BOT, new Vector2f(rightPane.getCenter().x, rightPane.getCenter().y + rightPane.getSize().y * 0.5f + 10.0f), 32.0f, "Chest");
        rightTexts = new ArrayList<>();
    }

    @Override
    public void onKeyPressed(int key) {
        switch (key) {
            case GLFW_KEY_UP:
                --index;

                if (side) {
                    while (index < 0)
                        index += rightInventory.getNumSlots();
                    while (index >= rightInventory.getNumSlots())
                        index -= rightInventory.getNumSlots();
                } else {
                    while (index < 0)
                        index += leftInventory.getNumSlots();
                    while (index >= leftInventory.getNumSlots())
                        index -= leftInventory.getNumSlots();
                }
                break;
            case GLFW_KEY_DOWN:
                ++index;

                if (side) {
                    while (index < 0)
                        index += rightInventory.getNumSlots();
                    while (index >= rightInventory.getNumSlots())
                        index -= rightInventory.getNumSlots();
                } else {
                    while (index < 0)
                        index += leftInventory.getNumSlots();
                    while (index >= leftInventory.getNumSlots())
                        index -= leftInventory.getNumSlots();
                }
                break;
            case GLFW_KEY_LEFT:
            case GLFW_KEY_RIGHT:
                side = !side;

                if (side) {
                    if (index >= rightInventory.getNumSlots())
                        index = rightInventory.getNumSlots() - 1;
                } else {
                    if (index >= leftInventory.getNumSlots())
                        index = leftInventory.getNumSlots() - 1;
                }
                break;
            case GLFW_KEY_R:
                if (side) {
                    InventorySlot slot = rightInventory.getSlot(index);
                    if (!slot.isEmpty()) {
                        Item item = slot.getItem();
                        if (leftInventory.add(item, 1))
                            slot.remove(1);
                    }
                } else {
                    InventorySlot slot = leftInventory.getSlot(index);
                    if (!slot.isEmpty()) {
                        Item item = slot.getItem();
                        if (rightInventory.add(item, 1))
                            slot.remove(1);
                    }
                }

                break;
            case GLFW_KEY_E:
                getManager().remove(this);
                break;
        }
    }

    @Override
    public void draw() {
        getManager().push(this);

        for (int i = 0; i < leftInventory.getNumSlots(); ++i) {
            InventorySlot slot = leftInventory.getSlot(i);
            GuiText text = leftTexts.get(i);

            text.setText(slot.isEmpty() ? "" : slot.getItem().getName() + "-" + slot.getCount());

            if (!side && index == i) {
                Vector2f position = new Vector2f(leftPane.getCenter().x, leftPane.getCenter().y + leftPane.getSize().y * 0.5f - i * 32.0f);
                Vector2f size = new Vector2f(leftPane.getSize().x, 32.0f);
                new GuiComponent(getManager(), GuiOrigin.TOP, position, size, new Vector4f(0.0f, 0.0f, 0.0f, 0.9f)).draw();
            }
        }

        leftPane.draw();
        leftTitle.draw();

        for (GuiText text : leftTexts)
            text.draw();

        for (int i = 0; i < rightInventory.getNumSlots(); ++i) {
            InventorySlot slot = rightInventory.getSlot(i);
            GuiText text = rightTexts.get(i);

            text.setText(slot.isEmpty() ? "" : slot.getItem().getName() + "-" + slot.getCount());

            if (side && index == i) {
                Vector2f position = new Vector2f(rightPane.getCenter().x, rightPane.getCenter().y + rightPane.getSize().y * 0.5f - i * 32.0f);
                Vector2f size = new Vector2f(rightPane.getSize().x, 32.0f);
                new GuiComponent(getManager(), GuiOrigin.TOP, position, size, new Vector4f(0.0f, 0.0f, 0.0f, 0.9f)).draw();
            }
        }

        rightPane.draw();
        rightTitle.draw();

        for (GuiText text : rightTexts)
            text.draw();

        getManager().pop();
    }

    public void setLeftInventory(Inventory inventory) {
        leftInventory = inventory;
        leftTexts.clear();
        index = 0;

        float xOffs = leftPane.getCenter().x - 0.5f * leftPane.getSize().x + 10.0f;
        float yOffs = leftPane.getCenter().y + 0.5f * leftPane.getSize().y;

        for (int i = 0; i < leftInventory.getNumSlots(); ++i) {
            leftTexts.add(new GuiText(getManager(), GuiOrigin.TOP_LEFT, new Vector2f(xOffs, yOffs), 32.0f));
            yOffs -= 32.0f;
        }
    }

    public void setRightInventory(Inventory inventory) {
        rightInventory = inventory;
        rightTexts.clear();
        index = 0;

        float xOffs = rightPane.getCenter().x - 0.5f * rightPane.getSize().x + 10.0f;
        float yOffs = rightPane.getCenter().y + 0.5f * rightPane.getSize().y;

        for (int i = 0; i < rightInventory.getNumSlots(); ++i) {
            rightTexts.add(new GuiText(getManager(), GuiOrigin.TOP_LEFT, new Vector2f(xOffs, yOffs), 32.0f));
            yOffs -= 32.0f;
        }
    }
}
