package me.marlon.game;

import static org.lwjgl.glfw.GLFW.*;

import me.marlon.gfx.Renderer;
import me.marlon.gfx.Window;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class Engine implements AutoCloseable {
    private Window window;
    private Renderer renderer;

    private float deltaTime;
    private World world;

    public Engine(int width, int height, String title, float deltaTime) {
        this.window = new Window(width, height, title);
        this.renderer = new Renderer();
        this.deltaTime = deltaTime;
        this.world = new World(this);

        glfwSetKeyCallback(window.getHandle(), new GLFWKeyCallback() {
            public void invoke(long window, int key, int scancode, int action, int mods) {
                switch (action) {
                    case GLFW_PRESS:
                        world.onKeyPressed(key);
                        break;
                    case GLFW_RELEASE:
                        world.onKeyReleased(key);
                        break;
                }
            }
        });

        glfwSetMouseButtonCallback(window.getHandle(), new GLFWMouseButtonCallback() {
            public void invoke(long window, int button, int action, int mods) {
                switch (action) {
                    case GLFW_PRESS:
                        world.onButtonPressed(button);
                        break;
                    case GLFW_RELEASE:
                        world.onButtonReleased(button);
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
                world.onMouseMoved(new Vector2f(x, y), new Vector2f(dx, dy));
            }
        });
    }

    public void update() {
        renderer.clear();
        world.onUpdate();
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

    public World getWorld() {
        return world;
    }
}
