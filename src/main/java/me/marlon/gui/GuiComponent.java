package me.marlon.gui;

import me.marlon.game.IMouseListener;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class GuiComponent implements IMouseListener {
    private GuiOrigin origin;
    private Vector2f position;
    private Vector2f size;
    private Vector4f color;

    public GuiComponent(GuiOrigin origin, Vector2f position, Vector2f size, Vector4f color) {
        this.origin = origin;
        this.position = position;
        this.size = size;
        this.color = color;
    }

    protected void onButtonPressedImpl(int button, Vector2f position) {
    }

    @Override
    public final void onButtonPressed(int button, Vector2f position) {
        position = new Vector2f(position).sub(getCenter());
        if (position.x > 0.5f * size.x)
            return;
        if (position.x < -0.5f * size.x)
            return;
        if (position.y > 0.5f * size.y)
            return;
        if (position.y < -0.5f * size.y)
            return;
        onButtonPressedImpl(button, position);
    }

    protected void onButtonReleasedImpl(int button, Vector2f position) {
    }

    @Override
    public final void onButtonReleased(int button, Vector2f position) {
        position = new Vector2f(position).sub(getCenter());
        if (position.x > 0.5f * size.x)
            return;
        if (position.x < -0.5f * size.x)
            return;
        if (position.y > 0.5f * size.y)
            return;
        if (position.y < -0.5f * size.y)
            return;
        onButtonPressedImpl(button, position);
    }

    protected void onMouseMovedImpl(Vector2f position, Vector2f velocity) {
    }

    @Override
    public final void onMouseMoved(Vector2f position, Vector2f velocity) {
        position = new Vector2f(position).sub(getCenter());
        if (position.x > 0.5f * size.x)
            return;
        if (position.x < -0.5f * size.x)
            return;
        if (position.y > 0.5f * size.y)
            return;
        if (position.y < -0.5f * size.y)
            return;
        onMouseMovedImpl(position, velocity);
    }

    public void draw(GuiManager gui) {
        gui.push(this);
        gui.rect(size, color);
        gui.pop();
    }

    public GuiOrigin getOrigin() {
        return origin;
    }

    public void setOrigin(GuiOrigin origin) {
        this.origin = origin;
    }

    public Vector2f getPosition() {
        return position;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public Vector2f getCenter() {
        Vector2f center = new Vector2f();

        switch (origin) {
            case TOP_LEFT:
                center.set(position.x + size.x * 0.5f, position.y - size.y * 0.5f);
                break;
            case TOP:
                center.set(position.x, position.y - size.y * 0.5f);
                break;
            case TOP_RIGHT:
                center.set(position.x - size.x * 0.5f, position.y - size.y * 0.5f);
                break;
            case MID_LEFT:
                center.set(position.x + size.x * 0.5f, position.y);
                break;
            case MID:
                center.set(position.x, position.y);
                break;
            case MID_RIGHT:
                center.set(position.x - size.x * 0.5f, position.y);
                break;
            case BOT_LEFT:
                center.set(position.x + size.x * 0.5f, position.y + size.y * 0.5f);
                break;
            case BOT:
                center.set(position.x, position.y + size.y * 0.5f);
                break;
            case BOT_RIGHT:
                center.set(position.x - size.x * 0.5f, position.y + size.y * 0.5f);
                break;
        }

        return center;
    }

    public Vector2f getSize() {
        return size;
    }

    public void setSize(Vector2f size) {
        this.size = size;
    }

    public Vector4f getColor() {
        return color;
    }

    public void setColor(Vector4f color) {
        this.color = color;
    }
}
