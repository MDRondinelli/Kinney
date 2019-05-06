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
        this.position = new Vector3f(position);
        this.velocity = new Vector3f(velocity);
        this.acceleration = new Vector3f(acceleration);
        this.damping = damping;
        this.invMass = invMass;

        force = new Vector3f();
    }

    public Particle(Vector3f position, Vector3f velocity, float damping, float invMass) {
        this.position = new Vector3f(position);
        this.velocity = new Vector3f(velocity);
        this.acceleration = new Vector3f(0.0f, 0.0f, 0.0f);
        this.damping = damping;
        this.invMass = invMass;

        force = new Vector3f();
    }

    public Particle(Vector3f position, float damping, float invMass) {
        this.position = new Vector3f(position);
        this.velocity = new Vector3f(0.0f, 0.0f, 0.0f);
        this.acceleration = new Vector3f(0.0f, 0.0f, 0.0f);
        this.damping = damping;
        this.invMass = invMass;

        force = new Vector3f();
    }

    public Particle(Vector3f position, Vector3f velocity, Vector3f acceleration, float invMass) {
        this.position = new Vector3f(position);
        this.velocity = new Vector3f(velocity);
        this.acceleration = new Vector3f(acceleration);
        this.damping = 0.995f;
        this.invMass = invMass;

        force = new Vector3f();
    }

    public Particle(Vector3f position, Vector3f velocity, float invMass) {
        this.position = new Vector3f(position);
        this.velocity = new Vector3f(velocity);
        this.acceleration = new Vector3f(0.0f, 0.0f, 0.0f);
        this.damping = 0.995f;
        this.invMass = invMass;

        force = new Vector3f();
    }

    public Particle(Vector3f position, float invMass) {
        this.position = new Vector3f(position);
        this.velocity = new Vector3f(0.0f, 0.0f, 0.0f);
        this.acceleration = new Vector3f(0.0f, 0.0f, 0.0f);
        this.damping = 0.995f;
        this.invMass = invMass;

        force = new Vector3f();
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
}
