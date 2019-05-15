package me.marlon.physics;

import org.joml.Vector3f;

public class BoundingSphere {
    private Vector3f center;
    private float radius;

    public BoundingSphere(Vector3f center, float radius) {
        this.center = center;
        this.radius = radius;
    }

    public BoundingSphere(BoundingSphere a, BoundingSphere b) {
        Vector3f r = new Vector3f(b.center).sub(a.center);
        float distance = r.length();

        Vector3f endpointB = new Vector3f(r).mul((distance + b.radius) / distance).add(a.center);
        r.negate();
        Vector3f endpointA = new Vector3f(r).mul((distance + a.radius) / distance).add(b.center);

        center = endpointA.mul(0.5f).add(endpointB.mul(0.5f));
        radius = distance + a.radius + b.radius;
    }

    public boolean overlaps(BoundingSphere other) {
        float r = radius + other.radius;
        return center.distanceSquared(other.center) < r * r;
    }

    public float getGrowth(BoundingSphere other) {
        float newRadius = center.distance(other.center) + radius + other.radius;
        return newRadius * newRadius - radius * radius;
    }

    public Vector3f getCenter() {
        return center;
    }

    public void setCenter(Vector3f center) {
        this.center = center;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
