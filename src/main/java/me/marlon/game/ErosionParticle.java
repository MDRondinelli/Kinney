package me.marlon.game;

import me.marlon.ecs.Terrain;
import org.joml.Vector2f;

public class ErosionParticle {
    private static final float INERTIA = 0.1f;
    private static final float CAPACITY = 1.0f;
    private static final float DEPOSITION = 0.1f;
    private static final float EROSION = 0.8f;
    private static final float EVAPORATION = 0.05f;
    private static final float RADIUS = 4.0f;
    private static final float MIN_SLOPE = 0.03f;
    private static final float GRAVITY = -6.0f;

    private Vector2f position;
    private Vector2f direction;
    private float speed;
    private float water;
    private float sediment;

    public ErosionParticle(Vector2f position, float water) {
        this.position = position;
        this.direction = new Vector2f();
        this.speed = 0.0f;
        this.water = water;
        this.sediment = 0.0f;
    }

    public void update(Terrain terrain) {
        if (water <= 0.0f)
            return;

        Vector2f oldPosition = new Vector2f(position);

        float hOld = terrain.sample(position.x, position.y);

        Vector2f gradient = terrain.gradient(position.x, position.y);
        direction.mul(INERTIA).sub(gradient.mul(1.0f - INERTIA));
        if (direction.x == 0.0f && direction.y == 0.0f) {
            float angle = (float) (2.0f * Math.PI * Math.random());
            direction.x = (float) Math.cos(angle);
            direction.y = (float) Math.sin(angle);
        }

        direction.normalize();
        position.add(direction);

        float hNew = terrain.sample(position.x, position.y);
        float hDif = hNew - hOld;

        if (hDif > 0.0f) {
            if (sediment >= hDif) {
                terrain.deposit(oldPosition.x, oldPosition.y, hDif);
                sediment -= hDif;
            } else {
                terrain.deposit(oldPosition.x, oldPosition.y, sediment);
                sediment = 0.0f;
            }
        } else if (hDif < 0.0f) {
            float c = Math.max(-hDif, MIN_SLOPE) * speed * water * CAPACITY;

            if (sediment > c) {
                float amt = (sediment - c) * DEPOSITION;
                sediment -= amt;

                terrain.deposit(oldPosition.x, oldPosition.y, amt);
            } else if (sediment < c) {
                float amt = Math.min((c - sediment) * EROSION, -hDif);
                sediment += amt;

                terrain.erode(oldPosition.x, oldPosition.y, RADIUS, amt);
            }
        }

        speed = (float) Math.sqrt(Math.max(speed * speed + hDif * GRAVITY, 0.0f));
        water *= 1.0f - EVAPORATION;
    }

    public float getWater() {
        return water;
    }
}
