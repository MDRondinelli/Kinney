package me.marlon.gui;

import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class GuiFrame extends GuiComponent {
    private List<GuiComponent> components;

    public GuiFrame(GuiOrigin origin, Vector2f position, Vector2f size, Vector4f color) {
        super(origin, position, size, color);
        components = new ArrayList<>();
    }

    @Override
    public void draw(GuiManager gui) {
        gui.push(this);
        gui.rect(getSize(), getColor());

        for (GuiComponent component : components)
            component.draw(gui);

        gui.pop();
    }

    public void add(GuiComponent component) {
        components.add(component);
    }

    public void remove(GuiComponent component) {
        components.remove(component);
    }
}
