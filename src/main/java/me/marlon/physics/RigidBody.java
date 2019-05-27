package me.marlon.physics;

import me.marlon.ecs.Terrain;
import org.joml.*;

import java.lang.Math;

public class RigidBody {
    public static RigidBody createTerrain(Terrain terrain) {
        return new RigidBody(new CollisionTerrain(terrain), 0.0f, new Matrix3f().zero(), new Vector3f());
    }

    public static RigidBody createPlane(Vector3f normal, float offset) {
        return new RigidBody(new CollisionPlane(normal, offset), 0.0f, new Matrix3f().zero(), new Vector3f());
    }

    public static RigidBody createCuboid(Vector3f halfExtents, float invMass, Vector3f position, Quaternionf orientation, Vector3f velocity, Vector3f acceleration, Vector3f rotation, float linearDamping, float angularDamping) {
        return new RigidBody(new CollisionBox(new Matrix4f(), halfExtents), invMass, getCuboidInverseTensor(1.0f / invMass, halfExtents.x * 2.0f, halfExtents.y * 2.0f, halfExtents.z * 2.0f), position, orientation, velocity, acceleration, rotation, linearDamping, angularDamping);
    }

    public static RigidBody createCuboid(Vector3f halfExtents, float invMass, Vector3f position, Quaternionf orientation, Vector3f velocity, Vector3f acceleration, Vector3f rotation) {
        return createCuboid(halfExtents, invMass, position, orientation, velocity, acceleration, rotation, 0.9f, 0.9f);
    }

    public static RigidBody createCuboid(Vector3f halfExtents, float invMass, Vector3f position, Quaternionf orientation) {
        return createCuboid(halfExtents, invMass, position, orientation, new Vector3f(), new Vector3f(), new Vector3f());
    }

    public static RigidBody createCuboid(Vector3f halfExtents, float invMass, Vector3f position) {
        return createCuboid(halfExtents, invMass, position, new Quaternionf());
    }

    public static RigidBody createSphere(float radius, float invMass, Vector3f position, Vector3f velocity, Vector3f acceleration, Vector3f rotation, float linearDamping, float angularDamping) {
        return new RigidBody(new CollisionSphere(new Vector3f(), radius), invMass, getSphereInverseTensor(1.0f / invMass, radius), position, new Quaternionf(), velocity, acceleration, rotation, linearDamping, angularDamping);
    }

    public static RigidBody createSphere(float radius, float invMass, Vector3f position, Vector3f velocity, Vector3f acceleration, Vector3f rotation) {
        return createSphere(radius, invMass, position, velocity, acceleration, rotation, 0.9f, 0.9f);
    }

    public static RigidBody createSphere(float radius, float invMass, Vector3f position) {
        return createSphere(radius, invMass, position, new Vector3f(), new Vector3f(), new Vector3f());
    }

    public static Matrix3f getCuboidInverseTensor(float m, float dx, float dy, float dz) {
        float dx2 = dx * dx;
        float dy2 = dy * dy;
        float dz2 = dz * dz;

        Matrix3f tensor = new Matrix3f();
        tensor.m00(m * (dy2 + dz2) / 12.0f);
        tensor.m11(m * (dx2 + dz2) / 12.0f);
        tensor.m22(m * (dx2 + dy2) / 12.0f);

        return tensor.invert();
    }

    public static Matrix3f getSphereInverseTensor(float m, float r) {
        float x = 0.4f * m * r * r;

        Matrix3f tensor = new Matrix3f();
        tensor.m00(x);
        tensor.m11(x);
        tensor.m22(x);

        return tensor.invert();
    }

    private CollisionPrimitive collider;

    private float invMass;
    private Matrix3f invInertiaTensor;

    private Vector3f position;
    private Quaternionf orientation;

    private Vector3f velocity;
    private Vector3f acceleration;
    private Vector3f accelerationAtUpdate;
    private Vector3f rotation;

    private float linearDamping;
    private float angularDamping;

    private Vector3f force;
    private Vector3f torque;

    private Matrix4f transform;
    private Matrix3f transformInvInertiaTensor;
    private AABBf transformVolume;

    public RigidBody(CollisionPrimitive collider, float invMass, Matrix3f invInertiaTensor,
                     Vector3f position, Quaternionf orientation, Vector3f velocity, Vector3f acceleration, Vector3f rotation,
                     float linearDamping, float angularDamping) {
        this.collider = collider;
        this.invMass = invMass;
        this.invInertiaTensor = invInertiaTensor;
        this.position = position;
        this.orientation = orientation;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.accelerationAtUpdate = new Vector3f();
        this.rotation = rotation;
        this.linearDamping = linearDamping;
        this.angularDamping = angularDamping;

        this.collider.setBody(this);

        force = new Vector3f();
        torque = new Vector3f();
        transform = new Matrix4f();
        transformInvInertiaTensor = new Matrix3f();
        transformVolume = new AABBf();
    }

    public RigidBody(CollisionPrimitive collider, float invMass, Matrix3f invInertiaTensor,
                     Vector3f position, Quaternionf orientation, Vector3f velocity, Vector3f acceleration, Vector3f rotation) {
        this(collider, invMass, invInertiaTensor, position, orientation, velocity, acceleration, rotation, 0.9f, 0.9f);
    }

    public RigidBody(CollisionPrimitive collider, float invMass, Matrix3f invInertiaTensor,
                     Vector3f position, Quaternionf orientation) {
        this(collider, invMass, invInertiaTensor, position, orientation, new Vector3f(), new Vector3f(), new Vector3f());
    }

    public RigidBody(CollisionPrimitive collider, float invMass, Matrix3f invInertiaTensor, Vector3f position) {
        this(collider, invMass, invInertiaTensor, position, new Quaternionf());
    }

    public void updateDerivedData() {
        transform.set(orientation);
        transform.setTranslation(position);

        float t4 = transform.m00() * invInertiaTensor.m00() + transform.m10() * invInertiaTensor.m01() + transform.m20() * invInertiaTensor.m02;
        float t9 = transform.m00() * invInertiaTensor.m10() + transform.m10() * invInertiaTensor.m11() + transform.m20() * invInertiaTensor.m12;
        float t14 = transform.m00() * invInertiaTensor.m20() + transform.m10() * invInertiaTensor.m21() + transform.m20() * invInertiaTensor.m22;
        float t28 = transform.m01() * invInertiaTensor.m00() + transform.m11() * invInertiaTensor.m01() + transform.m21() * invInertiaTensor.m02;
        float t33 = transform.m01() * invInertiaTensor.m10() + transform.m11() * invInertiaTensor.m11() + transform.m21() * invInertiaTensor.m12;
        float t38 = transform.m01() * invInertiaTensor.m20() + transform.m11() * invInertiaTensor.m21() + transform.m21() * invInertiaTensor.m22;
        float t52 = transform.m02() * invInertiaTensor.m00() + transform.m12() * invInertiaTensor.m01() + transform.m22() * invInertiaTensor.m02;
        float t57 = transform.m02() * invInertiaTensor.m10() + transform.m12() * invInertiaTensor.m11() + transform.m22() * invInertiaTensor.m12;
        float t62 = transform.m02() * invInertiaTensor.m20() + transform.m12() * invInertiaTensor.m21() + transform.m22() * invInertiaTensor.m22;
        transformInvInertiaTensor.m00(t4 * transform.m00() + t9 * transform.m10() + t14 * transform.m20());
        transformInvInertiaTensor.m10(t4 * transform.m01() + t9 * transform.m11() + t14 * transform.m21());
        transformInvInertiaTensor.m20(t4 * transform.m02() + t9 * transform.m12() + t14 * transform.m22());
        transformInvInertiaTensor.m01(t28 * transform.m00() + t33 * transform.m10() + t38 * transform.m20());
        transformInvInertiaTensor.m11(t28 * transform.m01() + t33 * transform.m11() + t38 * transform.m21());
        transformInvInertiaTensor.m21(t28 * transform.m02() + t33 * transform.m12() + t38 * transform.m22());
        transformInvInertiaTensor.m02(t52 * transform.m00() + t57 * transform.m10() + t62 * transform.m20());
        transformInvInertiaTensor.m12(t52 * transform.m01() + t57 * transform.m11() + t62 * transform.m21());
        transformInvInertiaTensor.m22(t52 * transform.m02() + t57 * transform.m12() + t62 * transform.m22());

        collider.updateDerivedData();
    }

    public void clearAccumulators() {
        force.zero();
        torque.zero();
    }

    public void addForce(Vector3f f) {
        force.add(f);
    }

    public void addForceAtBodyPoint(Vector3f f, Vector3f p) {
        p = transform.transformPosition(p, new Vector3f());
        addForceAtWorldPoint(f, p);
    }

    public void addForceAtWorldPoint(Vector3f f, Vector3f p) {
        force.add(f);
        torque.add(p.sub(position, new Vector3f()).cross(f));
    }

    public void addTorque(Vector3f t) {
        torque.add(t);
    }

    public void integrate(float dt) {
        Vector3f linearAcceleration = accelerationAtUpdate.set(acceleration).add(force.x * invMass, force.y * invMass, force.z * invMass);
        Vector3f angularAcceleration = transformInvInertiaTensor.transform(new Vector3f(torque));

        velocity.add(linearAcceleration.x * dt, linearAcceleration.y * dt, linearAcceleration.z * dt).mul((float) Math.pow(linearDamping, dt));
        rotation.add(angularAcceleration.x * dt, angularAcceleration.y * dt, angularAcceleration.z * dt).mul((float) Math.pow(angularDamping, dt));

        position.add(velocity.x * dt, velocity.y * dt, velocity.z * dt);
        Quaternionf q = new Quaternionf(rotation.x * dt, rotation.y * dt, rotation.z * dt, 0.0f).mul(orientation);
        orientation.add(q.x * 0.5f, q.y * 0.5f, q.z * 0.5f, q.w * 0.5f);
        orientation.normalize();

        clearAccumulators();
        updateDerivedData();
    }

    public CollisionPrimitive getCollider() {
        return collider;
    }

    public void setCollider(CollisionPrimitive collider) {
        this.collider = collider;
        this.collider.setBody(this);
    }

    public boolean hasFiniteMass() {
        return invMass != 0.0f;
    }

    public float getMass() {
        return 1.0f / invMass;
    }

    public void setMass(float mass) {
        invMass = 1.0f / mass;
    }

    public float getInvMass() {
        return invMass;
    }

    public void setInvMass(float invMass) {
        this.invMass = invMass;
    }

    public Matrix3f getInertiaTensor() {
        return invInertiaTensor.invert(new Matrix3f());
    }

    public void setInertiaTensor(Matrix3f inertiaTensor) {
        inertiaTensor.invert(invInertiaTensor);
    }

    public Matrix3f getInvInertiaTensor() {
        return invInertiaTensor;
    }

    public void setInvInertiaTensor(Matrix3f invInertiaTensor) {
        this.invInertiaTensor = invInertiaTensor;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Quaternionf getOrientation() {
        return orientation;
    }

    public void setOrientation(Quaternionf orientation) {
        this.orientation = orientation;
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

    public Vector3f getAccelerationAtUpdate() {
        return accelerationAtUpdate;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public float getLinearDamping() {
        return linearDamping;
    }

    public void setLinearDamping(float damping) {
        linearDamping = damping;
    }

    public float getAngularDamping() {
        return angularDamping;
    }

    public void setAngularDamping(float damping) {
        angularDamping = damping;
    }

    public Matrix4f getTransform() {
        return transform;
    }

    public Matrix3f getTransformInvInertiaTensor() {
        return transformInvInertiaTensor;
    }
}
