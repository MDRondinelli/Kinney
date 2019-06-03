package me.marlon.gui;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class GuiText extends GuiComponent {
    private static Vector2f getSize(String text) {
        String[] lines = text.split("\r\n|\r|\n");

        int rows = lines.length;
        int cols = 0;

        for (String line : lines)
            if (cols < line.length())
                cols = line.length();

        return new Vector2f(cols * 0.75f, rows);
    }

    private float scale;
    private String text;

    public GuiText(GuiManager manager, GuiOrigin origin, Vector2f position, float scale, String text) {
        super(manager, origin, position, getSize(text).mul(scale), new Vector4f(1.0f));
        this.scale = scale;
        this.text = text;
    }

    public GuiText(GuiManager manager, GuiOrigin origin, Vector2f position, float scale) {
        super(manager, origin, position, new Vector2f(), new Vector4f(1.0f));
        this.scale = scale;
        this.text = "";
    }

    @Override
    public void draw() {
        if (!text.isEmpty()) {
            getManager().push(this);
            getManager().text(getSize(), getColor(), text);
            getManager().pop();
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        setSize(getSize(text).mul(scale));
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
        setSize(getSize(text).mul(scale));
    }
}
