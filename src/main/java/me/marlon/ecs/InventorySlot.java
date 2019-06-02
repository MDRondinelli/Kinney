package me.marlon.ecs;

import me.marlon.game.Item;

public class InventorySlot {
    private Inventory inventory;
    private Item item;
    private int count;

    public InventorySlot(Inventory inventory, Item item, int count/*, boolean selected*/) {
        this.inventory = inventory;
        this.item = item;
        this.count = count;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item, int count) {
        this.item = item;
        this.count = count;
    }

    public int getCount() {
        return count;
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

        if (this.count == 0) {
            InventorySlot selection = inventory.getSlot(inventory.getSelection());
            if (selection == this)
                inventory.setSelection(0);

            inventory.getSlots().remove(this);
        }
    }
}
