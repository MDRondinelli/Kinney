package me.marlon.game;

import static org.lwjgl.glfw.GLFW.*;

import me.marlon.ecs.EntityManager;
import me.marlon.gfx.Renderer;
import me.marlon.gfx.Window;
import me.marlon.gui.*;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;

public class Engine implements AutoCloseable {
    private Window window;
    private GuiManager gui;
    private Renderer renderer;

    private float deltaTime;
    private EntityManager entities;

    public Engine(int width, int height, String title, float deltaTime) {
        this.window = new Window(width, height, title);
        this.gui = new GuiManager(window);
        this.renderer = new Renderer(gui);
        this.deltaTime = deltaTime;
        entities = new EntityManager(deltaTime, window, renderer);

        gui.add(new GuiComponent(GuiOrigin.MID, new Vector2f(), new Vector2f(8.0f), new Vector4f(1.0f)));

        glfwSetKeyCallback(window.getHandle(), new GLFWKeyCallback() {
            public void invoke(long window, int key, int scancode, int action, int mods) {
                switch (action) {
                    case GLFW_PRESS:
                        entities.onKeyPressed(key);
                        break;
                    case GLFW_RELEASE:
                        entities.onKeyReleased(key);
                        break;
                }
            }
        });

        glfwSetMouseButtonCallback(window.getHandle(), new GLFWMouseButtonCallback() {
            public void invoke(long window, int button, int action, int mods) {
                Vector2f position = new Vector2f();

                try (MemoryStack stack = MemoryStack.stackPush()) {
                    DoubleBuffer x = stack.mallocDouble(1);
                    DoubleBuffer y = stack.mallocDouble(1);
                    glfwGetCursorPos(window, x, y);
                    position.x = (float) x.get();
                    position.y = height - (float) y.get();
                }

                switch (action) {
                    case GLFW_PRESS:
                        gui.onButtonPressed(button, position);
                        entities.onButtonPressed(button, position);
                        break;
                    case GLFW_RELEASE:
                        gui.onButtonReleased(button, position);
                        entities.onButtonReleased(button, position);
                        break;
                }
            }
        });

        glfwSetCursorPosCallback(window.getHandle(), new GLFWCursorPosCallback() {
            private float x;
            private float y;
            private boolean initialized;

            public void invoke(long window, double xpos, double ypos) {
                if (!initialized) {
                    initialized = true;
                    x = (float) xpos;
                    y = (float) ypos;
                }

                float dx = (float) xpos - x;
                x = (float) xpos;
                float dy = (float) ypos - y;
                y = (float) ypos;

                Vector2f position = new Vector2f(x, height - y);
                Vector2f velocity = new Vector2f(dx, -dy);

                gui.onMouseMoved(position, velocity);
                entities.onMouseMoved(position, velocity);
            }
        });
    }

    public void update() {
        renderer.clear();
        entities.onUpdate();
        renderer.submitData();
    }

    public void render() {
        renderer.submitDraw();
    }

    public void run() {
        double previous = glfwGetTime();
        double lag = 0.0;

        double lastSecond = glfwGetTime();
        int frames = 0;

        while (!window.shouldClose()) {
            double current = glfwGetTime();
            double elapsed = current - previous;
            previous = current;
            lag += elapsed;

            window.pollEvents();

            while (lag >= deltaTime) {
                update();
                lag -= deltaTime;
            }

            frames++;
            render();
            window.swapBuffers();

            if (glfwGetTime() - lastSecond >= 1.0) {
                System.out.printf("frame time: %.3fms\n", 1000.0 / frames);
                frames = 0;
                lastSecond += 1.0;
            }
        }
    }

    public void close() {
        window.close();
        renderer.close();
    }

    public Window getWindow() {
        return window;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public float getDeltaTime() {
        return deltaTime;
    }

    public EntityManager getEntities() {
        return entities;
    }
}
