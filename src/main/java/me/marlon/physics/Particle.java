package me.marlon.physics;

import org.joml.Vector3f;

public class Particle {
    private Vector3f position;
    private Vector3f velocity;
    private Vector3f acceleration;
    private float damping;
    private float invMass;

    private Vector3f force;

    public Particle(Vector3f position, Vector3f velocity, Vector3f acceleration, float damping, float invMass) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.damping = damping;
        this.invMass = invMass;

        force = new Vector3f();
    }

    public Particle(Vector3f position, Vector3f velocity, float damping, float invMass) {
        this(position, velocity, new Vector3f(0.0f), damping, invMass);
    }

    public Particle(Vector3f position, float damping, float invMass) {
        this(position, new Vector3f(0.0f), new Vector3f(0.0f), damping, invMass);
    }

    public Particle(Vector3f position, Vector3f velocity, Vector3f acceleration, float invMass) {
        this(position, velocity, acceleration, 0.99f, invMass);
    }

    public Particle(Vector3f position, Vector3f velocity, float invMass) {
        this(position, velocity, new Vector3f(0.0f), 0.99f, invMass);
    }

    public Particle(Vector3f position, float invMass) {
        this(position, new Vector3f(0.0f), new Vector3f(0.0f), 0.99f, invMass);
    }

    public void clearForce() {
        force.zero();
    }

    public void addForce(Vector3f force) {
        this.force.add(force);
    }

    public void integrate(float dt) {
        position.add(velocity.x * dt, velocity.y * dt, velocity.z * dt);

        Vector3f acc = new Vector3f(acceleration);
        acc.add(force.x * invMass, force.y * invMass, force.z * invMass);
        velocity.add(acc.x * dt, acc.y * dt, acc.z * dt);
        velocity.mul((float) Math.pow(damping, dt));

        clearForce();
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public Vector3f getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Vector3f acceleration) {
        this.acceleration = acceleration;
    }

    public float getDamping() {
        return damping;
    }

    public void setDamping(float damping) {
        this.damping = damping;
    }

    public boolean hasFiniteMass() {
        return invMass != 0.0f;
    }

    public boolean hasInfiniteMass() {
        return invMass == 0.0f;
    }

    public float getMass() {
        return 1.0f / invMass;
    }

    public void setMass(float mass) {
        invMass = 1.0f / invMass;
    }

    public float getInvMass() {
        return invMass;
    }

    public void setInvMass(float invMass) {
        this.invMass = invMass;
    }
}
