package me.marlon.physics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public class CollisionDetector {
    public static void collide(CollisionSphere one, CollisionSphere two, List<Contact> contacts) {
        Vector3f positionOne = one.getWorldTransform().getTranslation(new Vector3f());
        Vector3f positionTwo = two.getWorldTransform().getTranslation(new Vector3f());

        Vector3f midline = new Vector3f(positionOne).sub(positionTwo);
        float distance = midline.length();

        if (distance <= 0.0f || distance >= one.getRadius() + two.getRadius())
            return;

        Vector3f point = new Vector3f(positionOne).add(midline.x * 0.5f, midline.y * 0.5f, midline.z * 0.5f);
        Vector3f normal = new Vector3f(midline).div(distance);
        float depth = one.getRadius() + two.getRadius() - distance;

        contacts.add(new Contact(point, normal, depth, one.getBody(), two.getBody()));
    }

    public static void collide(CollisionSphere sphere, CollisionPlane plane, List<Contact> contacts) {
        Vector3f position = sphere.getWorldTransform().getTranslation(new Vector3f());

        float distance = plane.getNormal().dot(position) - sphere.getRadius() - plane.getOffset();
        if (distance >= 0.0f)
            return;

        Vector3f point = position.sub(new Vector3f(plane.getNormal()).mul(distance + sphere.getRadius()));
        Vector3f normal = plane.getNormal();
        float depth = -distance;

        contacts.add(new Contact(point, normal, depth, sphere.getBody(), plane.getBody()));
    }

    public static void collide(CollisionPlane plane, CollisionSphere sphere, List<Contact> contacts) {
        collide(sphere, plane, contacts);
    }

    public static void collide(CollisionBox box, CollisionPlane plane, List<Contact> contacts) {
//        Matrix4f matrix = new Matrix4f(box.getTransform());
//        if (box.getBody() != null)
//            box.getBody().getTransform().mul(matrix, matrix);

        Vector3f vertex = new Vector3f();
        float vertexDistance = 0.0f;

        box.getWorldTransform().transformPosition(-box.getHalfExtents().x, -box.getHalfExtents().y, -box.getHalfExtents().z, vertex);
        vertexDistance = vertex.dot(plane.getNormal());

        if (vertexDistance <= plane.getOffset()) {
            Vector3f point = new Vector3f(plane.getNormal()).mul(0.5f * (vertexDistance - plane.getOffset())).add(vertex);
            Vector3f normal = new Vector3f(plane.getNormal());
            float depth = plane.getOffset() - vertexDistance;
            contacts.add(new Contact(point, normal, depth, box.getBody(), plane.getBody()));
        }

        box.getWorldTransform().transformPosition(-box.getHalfExtents().x, -box.getHalfExtents().y, box.getHalfExtents().z, vertex);
        vertexDistance = vertex.dot(plane.getNormal());

        if (vertexDistance <= plane.getOffset()) {
            Vector3f point = new Vector3f(plane.getNormal()).mul(0.5f * (vertexDistance - plane.getOffset())).add(vertex);
            Vector3f normal = new Vector3f(plane.getNormal());
            float depth = plane.getOffset() - vertexDistance;
            contacts.add(new Contact(point, normal, depth, box.getBody(), plane.getBody()));
        }

        box.getWorldTransform().transformPosition(-box.getHalfExtents().x, box.getHalfExtents().y, -box.getHalfExtents().z, vertex);
        vertexDistance = vertex.dot(plane.getNormal());

        if (vertexDistance <= plane.getOffset()) {
            Vector3f point = new Vector3f(plane.getNormal()).mul(0.5f * (vertexDistance - plane.getOffset())).add(vertex);
            Vector3f normal = new Vector3f(plane.getNormal());
            float depth = plane.getOffset() - vertexDistance;
            contacts.add(new Contact(point, normal, depth, box.getBody(), plane.getBody()));
        }

        box.getWorldTransform().transformPosition(-box.getHalfExtents().x, box.getHalfExtents().y, box.getHalfExtents().z, vertex);
        vertexDistance = vertex.dot(plane.getNormal());

        if (vertexDistance <= plane.getOffset()) {
            Vector3f point = new Vector3f(plane.getNormal()).mul(0.5f * (vertexDistance - plane.getOffset())).add(vertex);
            Vector3f normal = new Vector3f(plane.getNormal());
            float depth = plane.getOffset() - vertexDistance;
            contacts.add(new Contact(point, normal, depth, box.getBody(), plane.getBody()));
        }

        box.getWorldTransform().transformPosition(box.getHalfExtents().x, -box.getHalfExtents().y, -box.getHalfExtents().z, vertex);
        vertexDistance = vertex.dot(plane.getNormal());

        if (vertexDistance <= plane.getOffset()) {
            Vector3f point = new Vector3f(plane.getNormal()).mul(0.5f * (vertexDistance - plane.getOffset())).add(vertex);
            Vector3f normal = new Vector3f(plane.getNormal());
            float depth = plane.getOffset() - vertexDistance;
            contacts.add(new Contact(point, normal, depth, box.getBody(), plane.getBody()));
        }

        box.getWorldTransform().transformPosition(box.getHalfExtents().x, -box.getHalfExtents().y, box.getHalfExtents().z, vertex);
        vertexDistance = vertex.dot(plane.getNormal());

        if (vertexDistance <= plane.getOffset()) {
            Vector3f point = new Vector3f(plane.getNormal()).mul(0.5f * (vertexDistance - plane.getOffset())).add(vertex);
            Vector3f normal = new Vector3f(plane.getNormal());
            float depth = plane.getOffset() - vertexDistance;
            contacts.add(new Contact(point, normal, depth, box.getBody(), plane.getBody()));
        }

        box.getWorldTransform().transformPosition(box.getHalfExtents().x, box.getHalfExtents().y, -box.getHalfExtents().z, vertex);
        vertexDistance = vertex.dot(plane.getNormal());

        if (vertexDistance <= plane.getOffset()) {
            Vector3f point = new Vector3f(plane.getNormal()).mul(0.5f * (vertexDistance - plane.getOffset())).add(vertex);
            Vector3f normal = new Vector3f(plane.getNormal());
            float depth = plane.getOffset() - vertexDistance;
            contacts.add(new Contact(point, normal, depth, box.getBody(), plane.getBody()));
        }

        box.getWorldTransform().transformPosition(box.getHalfExtents().x, box.getHalfExtents().y, box.getHalfExtents().z, vertex);
        vertexDistance = vertex.dot(plane.getNormal());

        if (vertexDistance <= plane.getOffset()) {
            Vector3f point = new Vector3f(plane.getNormal()).mul(0.5f * (vertexDistance - plane.getOffset())).add(vertex);
            Vector3f normal = new Vector3f(plane.getNormal());
            float depth = plane.getOffset() - vertexDistance;
            contacts.add(new Contact(point, normal, depth, box.getBody(), plane.getBody()));
        }
    }

    public static void collide(CollisionPlane plane, CollisionBox box, List<Contact> contacts) {
        collide(box, plane, contacts);
    }

//    public static void collide(CollisionBox box, CollisionSphere sphere, List<Contact> contacts) {
//        Vector3f center = sphere.getWorldTransform().getTranslation(new Vector3f());
//        new Matrix4f(box.getWorldTransform()).invertAffine().transformPosition(center);
//
//        if (Math.abs(center.x) - sphere.getRadius() > box.getHalfExtents().x ||
//                Math.abs(center.y) - sphere.getRadius() > box.getHalfExtents().y ||
//                Math.abs(center.z) - sphere.getRadius() > box.getHalfExtents().z)
//            return;
//
//        Vector3f closest = new Vector3f();
//
//        if (center.x > box.getHalfExtents().x)
//            closest.x = box.getHalfExtents().x;
//        else if (center.x < -box.getHalfExtents().x)
//            closest.x = -box.getHalfExtents().x;
//
//        if (center.y > box.getHalfExtents().y)
//            closest.y = box.getHalfExtents().y;
//        else if (center.y < -box.getHalfExtents().y)
//            closest.y = -box.getHalfExtents().y;
//
//        if (center.z > box.getHalfExtents().z)
//            closest.z = box.getHalfExtents().z;
//        else if (center.z < -box.getHalfExtents().z)
//            closest.z = -box.getHalfExtents().z;
//    }
}
