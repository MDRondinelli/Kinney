package me.marlon.game;

import static org.lwjgl.glfw.GLFW.*;

import me.marlon.ecs.*;
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
import java.util.ArrayList;
import java.util.List;

public class Engine implements AutoCloseable {
    private Window window;
    private GuiManager gui;
    private Renderer renderer;

    private float deltaTime;
    private EntityManager entities;
    private ItemManager items;

    private List<IUpdateListener> updateListeners;
    private List<IKeyListener> keyListeners;
    private List<IMouseListener> mouseListeners;

    private BlockSystem blockSystem;
    private CameraSystem cameraSystem;
    private MeshSystem meshSystem;
    private PhysicsSystem physicsSystem;
    private PlayerSystem playerSystem;
    private SunSystem sunSystem;
    private TerrainSystem terrainSystem;
    private WaterSystem waterSystem;

    public Engine(int width, int height, String title, float deltaTime) {
        this.window = new Window(width, height, title);
        this.gui = new GuiManager(window);
        this.renderer = new Renderer(gui);
        this.deltaTime = deltaTime;
        entities = new EntityManager();
        items = new ItemManager();

        updateListeners = new ArrayList<>();
        keyListeners = new ArrayList<>();
        mouseListeners = new ArrayList<>();

        updateListeners.add(gui);
        keyListeners.add(gui);
        mouseListeners.add(gui);

        blockSystem = new BlockSystem(entities, 400, 100, 400);
        cameraSystem = new CameraSystem(entities, renderer);
        meshSystem = new MeshSystem(entities, renderer);
        physicsSystem = new PhysicsSystem(entities, deltaTime);
        playerSystem = new PlayerSystem(entities, blockSystem, physicsSystem, window, gui);
        sunSystem = new SunSystem(entities, renderer);
        terrainSystem = new TerrainSystem(entities, renderer);
        waterSystem = new WaterSystem(entities, renderer);

        entities.addComponentListener(blockSystem);
        entities.addComponentListener(cameraSystem);
        entities.addComponentListener(meshSystem);
        entities.addComponentListener(physicsSystem);
        entities.addComponentListener(playerSystem);
        entities.addComponentListener(sunSystem);
        entities.addComponentListener(terrainSystem);
        entities.addComponentListener(waterSystem);

        updateListeners.add(blockSystem);
        updateListeners.add(cameraSystem);
        updateListeners.add(meshSystem);
        updateListeners.add(physicsSystem);
        updateListeners.add(playerSystem);
        updateListeners.add(sunSystem);
        updateListeners.add(terrainSystem);
        updateListeners.add(waterSystem);

        keyListeners.add(playerSystem);
        mouseListeners.add(playerSystem);

        gui.add(new GuiComponent(gui, GuiOrigin.MID, new Vector2f(), new Vector2f(8.0f), new Vector4f(1.0f)));
//        gui.add(new GuiText(GuiOrigin.TOP, new Vector2f(0.0f, height * 0.5f), 64.0f, "\"my game\" - jeff"));

        glfwSetKeyCallback(window.getHandle(), new GLFWKeyCallback() {
            public void invoke(long window, int key, int scancode, int action, int mods) {
                switch (action) {
                    case GLFW_PRESS:
                        for (IKeyListener listener : keyListeners)
                            listener.onKeyPressed(key);
                        break;
                    case GLFW_RELEASE:
                        for (IKeyListener listener : keyListeners)
                            listener.onKeyReleased(key);
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
                        for (IMouseListener listener : mouseListeners)
                            listener.onButtonPressed(button, position);
                        break;
                    case GLFW_RELEASE:
                        for (IMouseListener listener : mouseListeners)
                            listener.onButtonReleased(button, position);
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

                for (IMouseListener listener : mouseListeners)
                    listener.onMouseMoved(position, velocity);
            }
        });
    }

    public void update() {
        renderer.clear();

        for (IUpdateListener listener : updateListeners)
            listener.onUpdate();

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
        renderer.close();
        gui.close();
        window.close();
    }

    public Window getWindow() {
        return window;
    }

    public GuiManager getGui() {
        return gui;
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

    public ItemManager getItems() {
        return items;
    }

    public BlockSystem getBlockSystem() {
        return blockSystem;
    }

    public CameraSystem getCameraSystem() {
        return cameraSystem;
    }

    public MeshSystem getMeshSystem() {
        return meshSystem;
    }

    public PhysicsSystem getPhysicsSystem() {
        return physicsSystem;
    }

    public PlayerSystem getPlayerSystem() {
        return playerSystem;
    }

    public SunSystem getSunSystem() {
        return sunSystem;
    }

    public TerrainSystem getTerrainSystem() {
        return terrainSystem;
    }

    public WaterSystem getWaterSystem() {
        return waterSystem;
    }
}
