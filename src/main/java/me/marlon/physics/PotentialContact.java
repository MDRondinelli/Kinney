package me.marlon.physics;

public class PotentialContact {
    private Collider colliderA;
    private Collider colliderB;

    public PotentialContact(Collider colliderA, Collider colliderB) {
        this.colliderA = colliderA;
        this.colliderB = colliderB;
    }

    public Collider getColliderA() {
        return colliderA;
    }

    public Collider getColliderB() {
        return colliderB;
    }
}
