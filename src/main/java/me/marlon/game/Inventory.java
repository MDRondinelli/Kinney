package me.marlon.game;

public class Inventory {
    private InventorySlot[] slots;

    public Inventory(int numSlots) {
        slots = new InventorySlot[numSlots];
        for (int i = 0; i < slots.length; ++i)
            slots[i] = new InventorySlot(slots, i);
    }

    public boolean add(Item item, int count) {
        for (InventorySlot slot : slots) {
            if (slot.getItem() == item) {
                slot.add(count);
                return true;
            }
        }

        for (InventorySlot slot : slots) {
            if (slot.isEmpty()) {
                slot.setItem(item, count);
                return true;
            }
        }

        return false;
    }

    public void remove(Item item, int count) {
        for (InventorySlot slot : slots) {
            if (slot.getItem() == item) {
                slot.remove(count);
                return;
            }
        }

        System.err.println("Cannot remove item that doesn't exist");
        System.exit(-1);
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
