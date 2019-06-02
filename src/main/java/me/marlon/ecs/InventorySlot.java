package me.marlon.ecs;

import me.marlon.game.Item;

public class InventorySlot {
    private InventorySlot[] slots;
    private int index;

    private Item item;
    private int count;

    public InventorySlot(InventorySlot[] slots, int index) {
        this.slots = slots;
        this.index = index;
    }

    public void clear() {
        item = null;
        count = 0;
    }

    public void add(int count) {
        if (count < 0) {
            System.out.println("Adding negative amount to inventory slot");
            System.exit(-1);
        }

        this.count += count;
    }

    public void remove(int count) {
        if (count < 0) {
            System.out.println("Removing negative amount from inventory slot");
            System.exit(-1);
        }

        if (count > this.count) {
            System.out.println("Removing more than inventory slot holds");
            System.exit(-1);
        }

        this.count -= count;

        if (this.count == 0)
            item = null;
    }

    public boolean isEmpty() {
        return item == null || count == 0;
    }

    public int getIndex() {
        return index;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item, int count) {
        if (item == null) {
            System.out.println("setItem(null, ...)!");
            System.exit(-1);
        }

        if (count <= 0) {
            System.out.println("setItem(..., 0)!");
            System.exit(-1);
        }

        this.item = item;
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
