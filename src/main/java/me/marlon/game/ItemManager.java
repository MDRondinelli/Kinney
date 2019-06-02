package me.marlon.game;

import java.util.HashMap;
import java.util.Map;

public class ItemManager {
    private Map<String, Item> items;

    public ItemManager() {
        items = new HashMap<>();
    }

    public void add(Item item) {
        items.put(item.getName(), item);
    }

    public Item get(String name) {
        return items.get(name);
    }
}
