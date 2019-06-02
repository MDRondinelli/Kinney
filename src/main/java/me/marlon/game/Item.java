package me.marlon.game;

public class Item {
    private String name;

    public Item(String name) {
        this.name = name;
    }

    public boolean use(int user) {
        return false;
    }

    public String getName() {
        return name;
    }
}
