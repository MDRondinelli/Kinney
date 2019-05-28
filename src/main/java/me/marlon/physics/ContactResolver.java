package me.marlon.physics;

import org.joml.Vector3f;

import java.util.List;

public class ContactResolver {
    private int positionIterations;
    private float positionEpsilon;
    private int velocityIterations;
    private float velocityEpsilon;

    public ContactResolver(int positionIterations, float positionEpsilon, int velocityIterations, float velocityEpsilon) {
        this.positionIterations = positionIterations;
        this.positionEpsilon = positionEpsilon;
        this.velocityIterations = velocityIterations;
        this.velocityEpsilon = velocityEpsilon;
    }

    private void prepareContacts(List<Contact> contacts, float dt) {
        for (Contact contact : contacts)
            contact.calcData(dt);
    }

    private void adjustPositions(List<Contact> contacts) {
        Vector3f[] linearChange = new Vector3f[2];
        linearChange[0] = new Vector3f();
        linearChange[1] = new Vector3f();
        Vector3f[] angularChange = new Vector3f[2];
        angularChange[0] = new Vector3f();
        angularChange[1] = new Vector3f();

        for (int numPositionIterations = 0; numPositionIterations < positionIterations; numPositionIterations++) {
            float max = positionEpsilon;
            Contact cMax = null;

            for (Contact c : contacts) {
                if (c.getDepth() > max) {
                    max = c.getDepth();
                    cMax = c;
                }
            }

            if (cMax == null)
                break;

            cMax.matchAwakeState();
            cMax.applyPositionChange(linearChange, angularChange);

            for (Contact c : contacts)
                for (int b = 0; b < 2; ++b)
                    if (c.getBody(b) != null)
                        for (int d = 0; d < 2; ++d)
                            if (c.getBody(b) == cMax.getBody(d))
                                c.setDepth(c.getDepth() + new Vector3f(angularChange[d]).cross(c.getRelPoint(b)).add(linearChange[d]).dot(c.getNormal()) * (b != 0 ? 1.0f : -1.0f));
        }
    }

    private void adjustVelocities(List<Contact> contacts, float dt) {
        Vector3f[] velocityChange = new Vector3f[2];
        velocityChange[0] = new Vector3f();
        velocityChange[1] = new Vector3f();

        Vector3f[] rotationChange = new Vector3f[2];
        rotationChange[0] = new Vector3f();
        rotationChange[1] = new Vector3f();

        for (int velocityIterationsUsed = 0; velocityIterationsUsed < velocityIterations; ++velocityIterationsUsed) {
            float max = velocityEpsilon;

            Contact cMax = null;

            for (Contact c : contacts) {
                if (c.getDesiredDeltaVelocity() > max) {
                    max = c.getDesiredDeltaVelocity();
                    cMax = c;
                }
            }

            if (cMax == null)
                break;

            cMax.matchAwakeState();
            cMax.applyVelocityChange(velocityChange, rotationChange);

            for (Contact c : contacts) {
                for (int b = 0; b < 2; ++b) {
                    if (c.getBody(b) != null) {
                        for (int d = 0; d < 2; ++d) {
                            if (c.getBody(b) == cMax.getBody(d)) {
                                Vector3f deltaVelocity = new Vector3f(rotationChange[d])
                                        .cross(c.getRelPoint(b))
                                        .add(velocityChange[d])
                                        .mul(c.getWorldToContact())
                                        .mul(b != 0 ? -1.0f : 1.0f);
                                c.getContactVelocity().add(deltaVelocity);
                                c.calcDesiredDeltaVelocity(dt);
                            }
                        }
                    }
                }
            }
        }
    }

    public void resolveContacts(List<Contact> contacts, float dt) {
        prepareContacts(contacts, dt);
        adjustPositions(contacts);
        adjustVelocities(contacts, dt);
    }
}
