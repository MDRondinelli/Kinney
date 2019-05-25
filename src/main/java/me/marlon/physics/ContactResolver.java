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
        for (int i = 0; i < contacts.size(); ++i)
            contacts.get(i).calcData(dt);
    }

    private void adjustPositions(List<Contact> contacts) {
//        Vector3f linearChangeA = new Vector3f();
//        Vector3f linearChangeB = new Vector3f();
        Vector3f[] linearChange = new Vector3f[2];
        linearChange[0] = new Vector3f();
        linearChange[1] = new Vector3f();
        Vector3f[] angularChange = new Vector3f[2];
        angularChange[0] = new Vector3f();
        angularChange[1] = new Vector3f();
//        Vector3f deltaPosition = new Vector3f();

        for (int numPositionIterations = 0; numPositionIterations < positionIterations; numPositionIterations++) {
            float max = positionEpsilon;
            int index = -1;

            for (int i = 0; i < contacts.size(); ++i) {
                Contact c = contacts.get(i);
                if (c.getDepth() > max) {
                    max = c.getDepth();
                    index = i;
                }
            }

            if (index == -1)
                break;

            Contact cIndex = contacts.get(index);
            cIndex.applyPositionChange(linearChange, angularChange);

            for (int i = 0; i < contacts.size(); ++i) {
                Contact c = contacts.get(i);

                for (int b = 0; b < 2; ++b)
                    if (c.getBody(b) != null)
                        for (int d = 0; d < 2; ++d)
                            if (c.getBody(b) == cIndex.getBody(d))
                                c.setDepth(c.getDepth() + new Vector3f(angularChange[d]).cross(c.getRelPoint(b)).add(linearChange[d]).dot(c.getNormal()) * (b != 0 ? 1.0f : -1.0f));
            }
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
            int index = -1;

            for (int i = 0; i < contacts.size(); ++i) {
                Contact c = contacts.get(i);
                if (c.getDesiredDeltaVelocity() > max) {
                    max = c.getDesiredDeltaVelocity();
                    index = i;
                }
            }

            if (index == -1)
                break;

            Contact cIndex = contacts.get(index);
            cIndex.applyVelocityChange(velocityChange, rotationChange);

            for (int i = 0; i < contacts.size(); ++i) {
                Contact c = contacts.get(i);

                for (int b = 0; b < 2; ++b) {
                    if (c.getBody(b) != null) {
                        for (int d = 0; d < 2; ++d) {
                            if (c.getBody(b) == cIndex.getBody(d)) {
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
