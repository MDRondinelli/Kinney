package me.marlon.physics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public class CollisionBox extends CollisionPrimitive {
    public static final int FACE_NEGATIVE_X = 0;
    public static final int FACE_POSITIVE_X = 1;
    public static final int FACE_NEGATIVE_Y = 2;
    public static final int FACE_POSITIVE_Y = 3;
    public static final int FACE_NEGATIVE_Z = 4;
    public static final int FACE_POSITIVE_Z = 5;
    public static final int VERTEX_POS_X_POS_Y_POS_Z = 6;
    public static final int VERTEX_POS_X_POS_Y_NEG_Z = 7;
    public static final int VERTEX_POS_X_NEG_Y_POS_Z = 8;
    public static final int VERTEX_POS_X_NEG_Y_NEG_Z = 9;
    public static final int VERTEX_NEG_X_POS_Y_POS_Z = 10;
    public static final int VERTEX_NEG_X_POS_Y_NEG_Z = 11;
    public static final int VERTEX_NEG_X_NEG_Y_POS_Z = 12;
    public static final int VERTEX_NEG_X_NEG_Y_NEG_Z = 13;

    private Vector3f halfExtents;

    public CollisionBox(RigidBody body, Matrix4f transform, Vector3f halfExtents) {
        super(body, transform);
        this.halfExtents = halfExtents;
    }

    @Override
    protected void collideWith(CollisionSphere sphere, List<Contact> contacts) {
        CollisionDetector.collide(this, sphere, contacts);
    }

    @Override
    protected void collideWith(CollisionPlane plane, List<Contact> contacts) {
        CollisionDetector.collide(this, plane, contacts);
    }

    @Override
    protected void collideWith(CollisionBox box, List<Contact> contacts) {
        CollisionDetector.collide(this, box, contacts);
    }

    public Vector3f getHalfExtents() {
        return halfExtents;
    }

    public void setHalfExtents(Vector3f halfExtents) {
        this.halfExtents = halfExtents;
    }
}
