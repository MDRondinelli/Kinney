package me.marlon.gui;

import me.marlon.gfx.Texture;
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

        return new Vector2f(cols, rows);
    }

    private String text;

    public GuiText(GuiOrigin origin, Vector2f position, String text, float scale) {
        super(origin, position, getSize(text).mul(scale), new Vector4f(1.0f));
        this.text = text;
    }

    @Override
    public void draw(GuiManager gui) {
        gui.push(this);
        gui.text(getSize(), getColor(), text);
        gui.pop();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
