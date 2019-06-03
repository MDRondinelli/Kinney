package me.marlon.game;

public class Item {
    private String name;
    private float energy;

    public Item(String name, float energy) {
        this.name = name;
        this.energy = energy;
    }

    public Item(String name) {
        this(name, 0.0f);
    }

    public boolean use(int user) {
        return false;
    }

    public String getName() {
        return name;
    }

    private float getEnergy() {
        return energy;
    }
}
