package me.marlon.gui;

import me.marlon.ecs.IUpdateListener;
import me.marlon.game.IKeyListener;
import me.marlon.game.IMouseListener;
import me.marlon.gfx.Shader;
import me.marlon.gfx.Texture;
import me.marlon.gfx.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.lwjgl.opengl.GL45.*;

public class GuiManager implements AutoCloseable, IKeyListener, IMouseListener, IUpdateListener {
    private static final int FONT_GLYPH_SIZE = 8;
    private static final int FONT_ATLAS_SIZE = 128;
    private static final int GLYPHS_PER_ROW = FONT_ATLAS_SIZE / FONT_GLYPH_SIZE;
    private static final String FONT_STRING = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[/]^_`abcdefghijklmnopqrstuvwxyz{|}~";

    private Window window;

    private Shader guiShader;
    private Shader textShader;

    private int squareVao;
    private int squareVbo;
    private int squareIbo;

    private Texture font;

    private List<Vector2f> translations;
    private List<GuiLayer> layers;
//    private Set<GuiComponent> components;
//    private List<GuiComponent> added;
//    private List<GuiComponent> removed;

    public GuiManager(Window window) {
        this.window = window;

        guiShader = new Shader();
        textShader = new Shader();

        try {
            guiShader.setVertText(Files.readString(Paths.get("res/shaders/gui.vert")));
            guiShader.setFragText(Files.readString(Paths.get("res/shaders/gui.frag")));

            textShader.setVertText(Files.readString(Paths.get("res/shaders/text.vert")));
            textShader.setFragText(Files.readString(Paths.get("res/shaders/text.frag")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        guiShader.compile();
        textShader.compile();

        float width = window.getFramebufferWidth();
        float height = window.getFramebufferHeight();
        Matrix4f ortho = new Matrix4f().ortho(-width / 2.0f, width / 2.0f,
                -height / 2.0f, height / 2.0f,
                0.0f, 1.0f);

        guiShader.set("proj", ortho);
        textShader.set("proj", ortho);
        textShader.set("texcoordScale", (float) FONT_GLYPH_SIZE / (float) FONT_ATLAS_SIZE);

        float[] squareVertices = new float[] {
                 1.0f,  1.0f,
                -1.0f,  1.0f,
                -1.0f, -1.0f,
                 1.0f, -1.0f
        };

        int[] squareIndices = new int[] {
                0, 1, 2,
                2, 3, 0
        };

        squareVao = glCreateVertexArrays();

        squareVbo = glCreateBuffers();
        glNamedBufferStorage(squareVbo, squareVertices, 0);
        glVertexArrayVertexBuffer(squareVao, 0, squareVbo, 0, 8);

        glEnableVertexArrayAttrib(squareVao, 0);
        glVertexArrayAttribFormat(squareVao, 0, 2, GL_FLOAT, false, 0);
        glVertexArrayAttribBinding(squareVao, 0, 0);

        squareIbo = glCreateBuffers();
        glNamedBufferStorage(squareIbo, squareIndices, 0);
        glVertexArrayElementBuffer(squareVao, squareIbo);

        font = Texture.fromFile("res/font.png", GL_R8);

        translations = new ArrayList<>();
        layers = new ArrayList<>();
//        components = new HashSet<>();
//        added = new ArrayList<>();
//        removed = new ArrayList<>();
    }

    @Override
    public void close() {
        guiShader.close();

        glDeleteBuffers(squareVbo);
        glDeleteBuffers(squareIbo);
        glDeleteVertexArrays(squareVao);

        font.close();
    }

    @Override
    public void onKeyPressed(int key) {
        for (GuiLayer layer : layers)
            layer.onKeyPressed(key);
    }

    @Override
    public void onKeyReleased(int key) {
        for (GuiLayer layer : layers)
            layer.onKeyReleased(key);
    }

    @Override
    public void onButtonPressed(int button, Vector2f position) {
        if (window.isMouseGrabbed())
            return;

        position = new Vector2f(position).sub(getWidth() / 2.0f, getHeight() / 2.0f);
        for (GuiLayer layer : layers)
            layer.onButtonPressed(button, position);
    }

    @Override
    public void onButtonReleased(int button, Vector2f position) {
        if (window.isMouseGrabbed())
            return;

        position = new Vector2f(position).sub(getWidth() / 2.0f, getHeight() / 2.0f);
        for (GuiLayer layer : layers)
            layer.onButtonReleased(button, position);
    }

    @Override
    public void onMouseMoved(Vector2f position, Vector2f velocity) {
        if (window.isMouseGrabbed())
            return;

        position = new Vector2f(position).sub(window.getFramebufferWidth() / 2.0f, window.getFramebufferHeight() / 2.0f);
        for (GuiLayer layer : layers)
            layer.onMouseMoved(position, velocity);
    }

    @Override
    public void onUpdate() {
        for (GuiLayer layer : layers)
            layer.onUpdate();
//        components.addAll(added);
//        components.removeAll(removed);
//        added.clear();
//        removed.clear();
    }

    public void push(GuiComponent component) {
        translations.add(component.getCenter());
    }

    public void pop() {
        translations.remove(translations.size() - 1);
    }

    public void rect(Vector2f size, Vector4f color) {
        guiShader.bind();

        Vector2f translation = new Vector2f();
        for (Vector2f v : translations)
            translation.add(v);

        Vector2f scale = new Vector2f(size).mul(0.5f);

        guiShader.set("translation", translation);
        guiShader.set("scale", scale);
        guiShader.set("color", color);

        glBindVertexArray(squareVao);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    }

    public void text(Vector2f size, Vector4f color, String str) {
        font.bind(0);

        textShader.bind();
        textShader.set("color", color);

        Vector2f translation = new Vector2f();
        for (Vector2f v : translations)
            translation.add(v);

        String[] lines = str.split("\r\n|\r|\n");
        float scale = size.y / lines.length;
        textShader.set("scale", 0.5f * size.y / lines.length);

        Vector2f corner = new Vector2f(translation.x - size.x * 0.5f + scale * 0.5f, translation.y + size.y * 0.5f - scale * 0.5f);

        glBindVertexArray(squareVao);
        for (int i = 0; i < lines.length; ++i) {
            String line = lines[i];
            for (int j = 0; j < line.length(); ++j) {
                char ch = line.charAt(j);
                int idx = FONT_STRING.indexOf(ch);
                if (idx == -1)
                    continue;

                textShader.set("translation", new Vector2f(corner).add(j * scale * 0.75f, i * scale));
                int texCoordOffsetX = idx % GLYPHS_PER_ROW;
                int texCoordOffsetY = idx / GLYPHS_PER_ROW;
                textShader.set("texcoordOffset", new Vector2f(texCoordOffsetX, texCoordOffsetY));
                glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
            }
        }
    }

    public void draw() {
        for (GuiLayer layer : layers)
            layer.draw();
    }

    public GuiLayer addLayer() {
        GuiLayer layer = new GuiLayer(this, getWidth(), getHeight());
        layers.add(layer);
        return layer;
    }

    public GuiLayer getLayer(int index) {
        return layers.get(index);
    }

//    public void add(GuiComponent component) {
//        added.add(component);
//    }
//
//    public void remove(GuiComponent component) {
//        removed.add(component);
//    }
//
//    public void toggle(GuiComponent component) {
//        if (components.contains(component))
//            remove(component);
//        else
//            add(component);
//    }

    public boolean isMouseGrabbed() {
        return window.isMouseGrabbed();
    }

    public int getWidth() {
        return window.getFramebufferWidth();
    }

    public int getHeight() {
        return window.getFramebufferHeight();
    }
}
