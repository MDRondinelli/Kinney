package me.marlon.ecs;

import me.marlon.game.Item;

public class Inventory {
    private InventorySlot[] slots;

    public Inventory(int numSlots) {
        slots = new InventorySlot[numSlots];
        for (int i = 0; i < slots.length; ++i)
            slots[i] = new InventorySlot(slots, i);
    }

    public void add(Item item, int count) {
        for (InventorySlot slot : slots) {
            if (slot.getItem() == item) {
                slot.add(count);
                return;
            }
        }

        for (InventorySlot slot : slots) {
            if (slot.isEmpty()) {
                slot.setItem(item, count);
                return;
            }
        }
    }

    public void remove(Item item, int count) {
        // DO NOT REPLACE WITH FOR-EACH: slot.remove() WILL THROW A CONCURRENT MODIFICATION EXCEPTION
//        for (int i = 0; i < slots.size(); ++i) {
//            InventorySlot slot = slots.get(i);
//
//            if (slot.getItem() == item) {
//                slot.remove(count);
//                return;
//            }
//        }

        for (InventorySlot slot : slots) {
            if (slot.getItem() == item) {
                slot.remove(count);
                return;
            }
        }

        System.err.println("Cannot remove item that doesn't exist");
        System.exit(-1);
    }

    public int getCount(Item item) {
        for (InventorySlot slot : slots)
            if (slot.getItem() == item)
                return slot.getCount();

        return 0;
    }

    public int getNumSlots() {
        return slots.length;
    }

    public InventorySlot getSlot(int index) {
        if (index < 0 || index >= slots.length)
            return null;
        else
            return slots[index];
    }

    public InventorySlot[] getSlots() {
        return slots;
    }
}
