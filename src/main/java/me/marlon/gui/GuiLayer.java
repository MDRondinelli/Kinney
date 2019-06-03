package me.marlon.gui;

import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GuiLayer extends GuiComponent {
    private Set<GuiComponent> components;
    private List<GuiComponent> added;
    private List<GuiComponent> removed;

    public GuiLayer(GuiManager manager, int width, int height) {
        super(manager, GuiOrigin.MID, new Vector2f(), new Vector2f(width, height), new Vector4f(0.0f));
        components = new HashSet<>();
        added = new ArrayList<>();
        removed = new ArrayList<>();
    }

    @Override
    public void onKeyPressed(int key) {
        for (GuiComponent component : components)
            component.onKeyPressed(key);
    }

    @Override
    public void onKeyReleased(int key) {
        for (GuiComponent component : components)
            component.onKeyReleased(key);
    }

    @Override
    public void onButtonPressedImpl(int button, Vector2f position) {
        for (GuiComponent component : components)
            component.onButtonPressed(button, position);
    }

    @Override
    public void onButtonReleasedImpl(int button, Vector2f position) {
        for (GuiComponent component : components)
            component.onButtonReleased(button, position);
    }

    @Override
    public void onMouseMovedImpl(Vector2f position, Vector2f velocity) {
        for (GuiComponent component : components)
            component.onMouseMoved(new Vector2f(position).sub(component.getCenter()), velocity);
    }

    @Override
    public void onUpdate() {
        components.addAll(added);
        components.removeAll(removed);
        added.clear();
        removed.clear();

        for (GuiComponent component : components)
            component.onUpdate();
    }

    public void draw() {
        for (GuiComponent component : components)
            component.draw();
    }

    public void clear() {
        components.clear();
        added.clear();
        removed.clear();
    }

    public void add(GuiComponent component) {
        added.add(component);
    }

    public void remove(GuiComponent component) {
        removed.add(component);
    }

    public void toggle(GuiComponent component) {
        if (components.contains(component))
            remove(component);
        else {
            clear();
            add(component);
        }
    }
}
