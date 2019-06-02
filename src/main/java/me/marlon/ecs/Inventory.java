package me.marlon.ecs;

import me.marlon.game.Item;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<InventorySlot> slots;
    private int selection;

    public Inventory() {
        slots = new ArrayList<>();
        selection = 0;
    }

    public void add(Item item, int count) {
        for (InventorySlot slot : slots) {
            if (slot.getItem() == item) {
                slot.add(count);
                return;
            }
        }

        slots.add(new InventorySlot(this, item, count));
    }

    public void remove(Item item, int count) {
        // DO NOT REPLACE WITH FOR-EACH: slot.remove() WILL THROW A CONCURRENT MODIFICATION EXCEPTION
        for (int i = 0; i < slots.size(); ++i) {
            InventorySlot slot = slots.get(i);

            if (slot.getItem() == item) {
                slot.remove(count);
                return;
            }
        }
    }

    public int getCount(Item item) {
        for (InventorySlot slot : slots)
            if (slot.getItem() == item)
                return slot.getCount();

        return 0;
    }

    public InventorySlot getSlot(int index) {
        if (index < 0 || index >= slots.size())
            return null;
        else
            return slots.get(index);
    }

    public List<InventorySlot> getSlots() {
        return slots;
    }

    public int getSelection() {
        return selection;
    }

    public void setSelection(int selection) {
        if (slots.size() != 0) {
            while (selection < 0)
                selection += slots.size();
            while (selection >= slots.size())
                selection -= slots.size();

            this.selection = selection;
        }
    }
}
