package me.marlon.physics;

import java.util.List;

public class BoundingVolumeHierarchy {
    private BoundingVolumeHierarchy parent;
    private BoundingVolumeHierarchy childA;
    private BoundingVolumeHierarchy childB;
    private BoundingSphere volume;

    private RigidBody body;

    private BoundingVolumeHierarchy(BoundingVolumeHierarchy parent, BoundingSphere volume, RigidBody body) {
        this.parent = parent;
        this.volume = volume;
        this.body = body;
    }

    public void insert(BoundingSphere newVolume, RigidBody newBody) {
        if (isLeaf()) {
            childA = new BoundingVolumeHierarchy(this, volume, body);
            childB = new BoundingVolumeHierarchy(this, newVolume, newBody);
            volume = new BoundingSphere(childA.volume, childB.volume);
            body = null;
        } else {
            if (childA.volume.getGrowth(newVolume) < childB.volume.getGrowth(newVolume))
                childA.insert(newVolume, newBody);
            else
                childB.insert(newVolume, newBody);
        }
    }

    public boolean overlaps(BoundingVolumeHierarchy other) {
        return volume.overlaps(other.volume);
    }

    public void getPotentialContacts(List<PotentialContact> contacts) {
        if (isLeaf())
            return;

        childA.getPotentialContacts(contacts, childB);
    }

    public void getPotentialContacts(List<PotentialContact> contacts, BoundingVolumeHierarchy other) {
        if (!overlaps(other))
            return;

        if (isLeaf() && other.isLeaf()) {
            contacts.add(new PotentialContact(body, other.body));
            return;
        }

        if (other.isLeaf() || (!isLeaf() && volume.getRadius() >= other.volume.getRadius())) {
            childA.getPotentialContacts(contacts, other);
            childB.getPotentialContacts(contacts, other);
        } else {
            getPotentialContacts(contacts, other.childA);
            getPotentialContacts(contacts, other.childB);
        }
    }

    public boolean isLeaf() {
        return body != null;
    }
}
