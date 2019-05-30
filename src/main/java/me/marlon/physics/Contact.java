package me.marlon.physics;

import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Contact {
    private Vector3f point;
    private Vector3f normal;
    private float depth;
    private float friction;

    private CollisionPrimitive primitiveA;
    private CollisionPrimitive primitiveB;

    private RigidBody bodyA;
    private RigidBody bodyB;

    private Matrix3f contactToWorld;
    private Matrix3f worldToContact;

    private Vector3f contactVelocity;

    private float desiredDeltaVelocity;

    private Vector3f relPointA;
    private Vector3f relPointB;

    public Contact(Vector3f point, Vector3f normal, float depth, CollisionPrimitive primitiveA, CollisionPrimitive primitiveB) {
        this.point = point;
        this.normal = normal;
        this.depth = depth;
        this.friction = 1.4f;
        this.primitiveA = primitiveA;
        this.primitiveB = primitiveB;
        this.bodyA = primitiveA.getBody();
        this.bodyB = primitiveB.getBody();
        contactToWorld = new Matrix3f();
        worldToContact = new Matrix3f();
        contactVelocity = new Vector3f();
        desiredDeltaVelocity = 0.0f;
        relPointA = new Vector3f();
        relPointB = new Vector3f();
    }

    private void calcTransforms() {
        contactToWorld.setColumn(0, normal);

        if (Math.abs(normal.x) > Math.abs(normal.y)) {
            float s = 1.0f / (float) Math.sqrt(normal.z * normal.z + normal.x * normal.x);

            float tangent0x = normal.z * s;
            float tangent0y = 0.0f;
            float tangent0z = -normal.x * s;

            float tangent1x = normal.y * tangent0x;
            float tangent1y = normal.z * tangent0x - normal.x * tangent0z;
            float tangent1z = -tangent1x;

            contactToWorld.setColumn(1, tangent0x, tangent0y, tangent0z);
            contactToWorld.setColumn(2, tangent1x, tangent1y, tangent1z);
        } else {
            float s = 1.0f / (float) Math.sqrt(normal.z * normal.z + normal.y * normal.y);

            float tangent0x = 0.0f;
            float tangent0y = -normal.z * s;
            float tangent0z = normal.y * s;

            float tangent1x = normal.y * tangent0z - normal.z * tangent0y;
            float tangent1y = -normal.x * tangent0z;
            float tangent1z = -tangent1y;

            contactToWorld.setColumn(1, tangent0x, tangent0y, tangent0z);
            contactToWorld.setColumn(2, tangent1x, tangent1y, tangent1z);
        }

        worldToContact.set(contactToWorld).transpose();
    }

    private Vector3f calcLocalVelocity(RigidBody body, Vector3f relPoint, float dt) {
        Vector3f velocity = new Vector3f(body.getRotation()).cross(relPoint).add(body.getVelocity()).mul(worldToContact);
        Vector3f acceleration = new Vector3f(body.getAccelerationAtUpdate()).mul(dt).mul(worldToContact);
        acceleration.x = 0.0f;
        velocity.add(acceleration);
        return velocity;
    }

    public void calcDesiredDeltaVelocity(float dt) {
        float velocityFromAcceleration = bodyA.getAccelerationAtUpdate().dot(normal) * dt;

        if (bodyB != null)
            velocityFromAcceleration -= bodyB.getAccelerationAtUpdate().dot(normal) * dt;

        float restitution = 0.15f;
        if (Math.abs(contactVelocity.x) < 0.1f)
            restitution = 0.0f;

        desiredDeltaVelocity = -contactVelocity.x - restitution * (contactVelocity.x - velocityFromAcceleration);
    }

    public void swap() {
        normal.negate();

        RigidBody bodyC = bodyA;
        bodyA = bodyB;
        bodyB = bodyC;

        CollisionPrimitive primitiveC = primitiveA;
        primitiveA = primitiveB;
        primitiveA = primitiveC;
    }

    public void calcData(float dt) {
        if (bodyA == null)
            swap();

        calcTransforms();

        relPointA.set(point).sub(bodyA.getPosition());
        if (bodyB != null)
            relPointB.set(point).sub(bodyB.getPosition());

        contactVelocity.set(calcLocalVelocity(bodyA, relPointA, dt));
        if (bodyB != null)
            contactVelocity.sub(calcLocalVelocity(bodyB, relPointB, dt));

        calcDesiredDeltaVelocity(dt);
    }

    public void applyPositionChange(Vector3f[] linearChange, Vector3f[] angularChange) {
        float linearInertiaA;
        float linearInertiaB = 0.0f;
        float angularInertiaA;
        float angularInertiaB = 0.0f;
        float totalInertia = 0.0f;

        {
            Vector3f angularInertiaWorld = new Vector3f(relPointA)
                    .cross(normal)
                    .mul(bodyA.getTransformInvInertiaTensor())
                    .cross(relPointA);

            angularInertiaA = angularInertiaWorld.dot(normal);
            linearInertiaA = bodyA.getInvMass();

            totalInertia += linearInertiaA + angularInertiaA;
        }

        if (bodyB != null)
        {
            Vector3f angularInertiaWorld = new Vector3f(relPointB)
                    .cross(normal)
                    .mul(bodyB.getTransformInvInertiaTensor())
                    .cross(relPointB);

            angularInertiaB = angularInertiaWorld.dot(normal);
            linearInertiaB = bodyB.getInvMass();

            totalInertia += linearInertiaB + angularInertiaB;
        }

        {
            float angularMove = depth * angularInertiaA / totalInertia;
            float linearMove = depth * linearInertiaA / totalInertia;

            Vector3f projection = new Vector3f(normal).mul(-relPointA.dot(normal)).add(relPointA);

            float maxMagnitude = 0.2f * projection.length();
            if (angularMove < -maxMagnitude) {
                float totalMove = angularMove + linearMove;
                angularMove = -maxMagnitude;
                linearMove = totalMove - angularMove;
            }
            else if (angularMove > maxMagnitude) {
                float totalMove = angularMove + linearMove;
                angularMove = maxMagnitude;
                linearMove = totalMove - angularMove;
            }

            if (angularMove == 0.0f)
                angularChange[0].zero();
            else
                angularChange[0]
                        .set(relPointA)
                        .cross(normal)
                        .mul(bodyA.getTransformInvInertiaTensor())
                        .mul(angularMove / angularInertiaA);

            linearChange[0].set(normal).mul(linearMove);

            bodyA.getPosition().add(linearChange[0]);

            Quaternionf q = new Quaternionf(angularChange[0].x, angularChange[0].y, angularChange[0].z, 0.0f);
            q.mul(bodyA.getOrientation());
            bodyA.getOrientation().add(q.x * 0.5f, q.y * 0.5f, q.z * 0.5f, q.w * 0.5f).normalize();
        }

        if (bodyB != null) {
            float angularMove = -depth * angularInertiaB / totalInertia;
            float linearMove = -depth * linearInertiaB / totalInertia;

            Vector3f projection = new Vector3f(normal).mul(-relPointB.dot(normal)).add(relPointB);

            float maxMagnitude = 0.2f * projection.length();
            if (angularMove < -maxMagnitude) {
                float totalMove = angularMove + linearMove;
                angularMove = -maxMagnitude;
                linearMove = totalMove - angularMove;
            } else if (angularMove > maxMagnitude) {
                float totalMove = angularMove + linearMove;
                angularMove = maxMagnitude;
                linearMove = totalMove - angularMove;
            }

            if (angularMove == 0.0f)
                angularChange[1].zero();
            else
                angularChange[1]
                        .set(relPointB)
                        .cross(normal)
                        .mul(bodyB.getTransformInvInertiaTensor())
                        .mul(angularMove / angularInertiaB);

            linearChange[1].set(normal).mul(linearMove);

            bodyB.getPosition().add(linearChange[1]);

            Quaternionf q = new Quaternionf(angularChange[1].x, angularChange[1].y, angularChange[1].z, 0.0f);
            q.mul(bodyB.getOrientation());
            bodyB.getOrientation().add(q.x * 0.5f, q.y * 0.5f, q.z * 0.5f, q.w * 0.5f).normalize();
        } else {
            linearChange[1].zero();
            angularChange[1].zero();
        }
    }

    private Vector3f calcImpulse() {
        float invMass = bodyA.getInvMass();

        Matrix3f impulseToTorque = new Matrix3f()
                .m00(0.0f)
                .m10(-relPointA.z)
                .m20(relPointA.y)
                .m01(relPointA.z)
                .m11(0.0f)
                .m21(-relPointA.x)
                .m02(-relPointA.y)
                .m12(relPointA.x)
                .m22(0.0f);

        Matrix3f deltaVelWorld = new Matrix3f(impulseToTorque)
                .mul(bodyA.getTransformInvInertiaTensor())
                .mul(impulseToTorque)
                .scale(-1.0f);

        if (bodyB != null) {
            invMass += bodyB.getInvMass();

            impulseToTorque
                    .m00(0.0f)
                    .m10(-relPointB.z)
                    .m20(relPointB.y)
                    .m01(relPointB.z)
                    .m11(0.0f)
                    .m21(-relPointB.x)
                    .m02(-relPointB.y)
                    .m12(relPointB.x)
                    .m22(0.0f);

            Matrix3f deltaVelWorldB = new Matrix3f(impulseToTorque)
                    .mul(bodyB.getTransformInvInertiaTensor())
                    .mul(impulseToTorque)
                    .scale(-1.0f);

            deltaVelWorld.add(deltaVelWorldB);
        }

        Matrix3f deltaVelContact = new Matrix3f(worldToContact).mul(deltaVelWorld).mul(contactToWorld);
        deltaVelContact.m00 += invMass;
        deltaVelContact.m11 += invMass;
        deltaVelContact.m22 += invMass;

        Matrix3f impulseMatrix = new Matrix3f(deltaVelContact).invert();
        Vector3f impulseContact = new Vector3f(desiredDeltaVelocity, -contactVelocity.y, -contactVelocity.z).mul(impulseMatrix);
        float planarImpulse = (float) Math.sqrt(impulseContact.y * impulseContact.y + impulseContact.z * impulseContact.z);
        if (planarImpulse > impulseContact.x * friction) {
            impulseContact.y /= planarImpulse;
            impulseContact.z /= planarImpulse;

            impulseContact.x = deltaVelContact.m00() +
                    deltaVelContact.m10() * friction * impulseContact.y +
                    deltaVelContact.m20() * friction * impulseContact.z;
            impulseContact.y *= friction * impulseContact.x;
            impulseContact.z *= friction * impulseContact.x;
        }

        return impulseContact.mul(contactToWorld);
    }

    public void applyVelocityChange(Vector3f[] velocityChange, Vector3f[] rotationChange) {
        Vector3f impulse = calcImpulse();

        velocityChange[0].set(impulse.x * bodyA.getInvMass(), impulse.y * bodyA.getInvMass(), impulse.z * bodyA.getInvMass());
        rotationChange[0].set(new Vector3f(relPointA).cross(impulse).mul(bodyA.getTransformInvInertiaTensor()));

        bodyA.getVelocity().add(velocityChange[0]);
        bodyA.getRotation().add(rotationChange[0]);

        if (bodyB != null) {
            velocityChange[1].set(impulse.x * -bodyB.getInvMass(), impulse.y * -bodyB.getInvMass(), impulse.z * -bodyB.getInvMass());
            rotationChange[1].set(new Vector3f(impulse).cross(relPointB).mul(bodyB.getTransformInvInertiaTensor()));

            bodyB.getVelocity().add(velocityChange[1]);
            bodyB.getRotation().add(rotationChange[1]);
        } else {
            velocityChange[1].zero();
            rotationChange[1].zero();
        }
    }

    public void matchAwakeState() {
        if (bodyB == null)
            return;

        boolean awakeA = bodyA.isAwake();
        boolean awakeB = bodyB.isAwake();

        if (awakeA && !awakeB)
            bodyB.setAwake(true);
        if (!awakeA && awakeB)
            bodyA.setAwake(true);
    }

    public Vector3f getPoint() {
        return point;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public float getDepth() {
        return depth;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }

    public float getFriction() {
        return friction;
    }

    public CollisionPrimitive getPrimitive(int index) {
        if (index == 0)
            return primitiveA;
        else
            return primitiveB;
    }

    public RigidBody getBody(int index) {
        if (index == 0)
            return bodyA;
        else
            return bodyB;
    }

    public Matrix3f getContactToWorld() {
        return contactToWorld;
    }

    public Matrix3f getWorldToContact() {
        return worldToContact;
    }

    public Vector3f getContactVelocity() {
        return contactVelocity;
    }

    public void setContactVelocity(Vector3f contactVelocity) {
        this.contactVelocity = contactVelocity;
    }

    public float getDesiredDeltaVelocity() {
        return desiredDeltaVelocity;
    }

    public Vector3f getRelPoint(int index) {
        if (index == 0)
            return relPointA;
        else if (index == 1)
            return relPointB;
        else
            return null;
    }
}
