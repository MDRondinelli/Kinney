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

    public static void collide(CollisionBox box, CollisionSphere sphere, List<Contact> contacts) {
        Vector3f center = sphere.getWorldTransform().getTranslation(new Vector3f());
        Vector3f relCenter = box.getWorldTransformInv().transformPosition(new Vector3f(center));

        if (Math.abs(relCenter.x) - sphere.getRadius() > box.getHalfExtents().x ||
                Math.abs(relCenter.y) - sphere.getRadius() > box.getHalfExtents().y ||
                Math.abs(relCenter.z) - sphere.getRadius() > box.getHalfExtents().z)
            return;

        Vector3f closest = new Vector3f();

        if (relCenter.x > box.getHalfExtents().x)
            closest.x = box.getHalfExtents().x;
        else if (relCenter.x < -box.getHalfExtents().x)
            closest.x = -box.getHalfExtents().x;

        if (relCenter.y > box.getHalfExtents().y)
            closest.y = box.getHalfExtents().y;
        else if (relCenter.y < -box.getHalfExtents().y)
            closest.y = -box.getHalfExtents().y;

        if (relCenter.z > box.getHalfExtents().z)
            closest.z = box.getHalfExtents().z;
        else if (relCenter.z < -box.getHalfExtents().z)
            closest.z = -box.getHalfExtents().z;

        float distance = closest.distanceSquared(relCenter);
        if (distance > sphere.getRadius() * sphere.getRadius())
            return;

        box.getWorldTransform().transformPosition(closest);

        Vector3f point = closest;
        Vector3f normal = center.sub(closest).normalize();
        float depth = sphere.getRadius() - (float) Math.sqrt(distance);
        contacts.add(new Contact(point, normal, depth, box.getBody(), sphere.getBody()));
    }

    public static void collide(CollisionSphere sphere, CollisionBox box, List<Contact> contacts) {
        collide(box, sphere, contacts);
    }

    private static float transformToAxis(CollisionBox box, Vector3f axis) {
        return box.getHalfExtents().x * Math.abs(axis.x * box.getWorldTransform().m00() + axis.y * box.getWorldTransform().m01() + axis.z * box.getWorldTransform().m02()) +
               box.getHalfExtents().y * Math.abs(axis.x * box.getWorldTransform().m10() + axis.y * box.getWorldTransform().m11() + axis.z * box.getWorldTransform().m12()) +
               box.getHalfExtents().x * Math.abs(axis.x * box.getWorldTransform().m20() + axis.y * box.getWorldTransform().m21() + axis.z * box.getWorldTransform().m22());
    }

    private static boolean overlapOnAxis(CollisionBox one, CollisionBox two, Vector3f axis) {
        float oneProject = transformToAxis(one, axis);
        float twoProject = transformToAxis(two, axis);

        float toCenterX = two.getWorldTransform().m30() - one.getWorldTransform().m30();
        float toCenterY = two.getWorldTransform().m31() - one.getWorldTransform().m31();
        float toCenterZ = two.getWorldTransform().m32() - one.getWorldTransform().m32();

        float distance = Math.abs(axis.dot(toCenterX, toCenterY, toCenterZ));
        return distance < oneProject + twoProject;
    }

    private static void collide(CollisionBox box, CollisionPrimitive other, Vector3f point, List<Contact> contacts) {
        Vector3f relPoint = box.getWorldTransformInv().transformPosition(new Vector3f(point));
        Vector3f normal = new Vector3f();

        float minDepth = box.getHalfExtents().x - Math.abs(relPoint.x);
        if (minDepth < 0)
            return;

        box.getWorldTransform().getColumn(0, normal).mul(relPoint.x < 0.0f ? -1.0f : 1.0f);

        float depth = box.getHalfExtents().y - Math.abs(relPoint.y);
        if (depth < 0)
            return;

        if (depth < minDepth) {
            minDepth = depth;
            box.getWorldTransform().getColumn(1, normal).mul(relPoint.y < 0.0f ? -1.0f : 1.0f);
        }

        depth = box.getHalfExtents().z - Math.abs(relPoint.z);
        if (depth < 0)
            return;

        if (depth < minDepth) {
            minDepth = depth;
            box.getWorldTransform().getColumn(2, normal).mul(relPoint.z < 0.0f ? -1.0f : 1.0f);
        }

        contacts.add(new Contact(point, normal, minDepth, box.getBody(), other.getBody()));
    }

    public static void collide(CollisionBox one, CollisionBox two, List<Contact> contacts) {
        Vector3f axis = new Vector3f();

        one.getWorldTransform().getColumn(0, axis);
        if (!overlapOnAxis(one, two, axis))
            return;

        one.getWorldTransform().getColumn(1, axis);
        if (!overlapOnAxis(one, two, axis))
            return;

        one.getWorldTransform().getColumn(2, axis);
        if (!overlapOnAxis(one, two, axis))
            return;

        two.getWorldTransform().getColumn(0, axis);
        if (!overlapOnAxis(one, two, axis))
            return;

        two.getWorldTransform().getColumn(1, axis);
        if (!overlapOnAxis(one, two, axis))
            return;

        two.getWorldTransform().getColumn(2, axis);
        if (!overlapOnAxis(one, two, axis))
            return;

        one.getWorldTransform().getColumn(0, axis).cross(two.getWorldTransform().m00(), two.getWorldTransform().m01(), two.getWorldTransform().m02());
        if (!overlapOnAxis(one, two, axis))
            return;

        one.getWorldTransform().getColumn(0, axis).cross(two.getWorldTransform().m10(), two.getWorldTransform().m11(), two.getWorldTransform().m12());
        if (!overlapOnAxis(one, two, axis))
            return;

        one.getWorldTransform().getColumn(0, axis).cross(two.getWorldTransform().m20(), two.getWorldTransform().m21(), two.getWorldTransform().m22());
        if (!overlapOnAxis(one, two, axis))
            return;

        one.getWorldTransform().getColumn(1, axis).cross(two.getWorldTransform().m00(), two.getWorldTransform().m01(), two.getWorldTransform().m02());
        if (!overlapOnAxis(one, two, axis))
            return;

        one.getWorldTransform().getColumn(1, axis).cross(two.getWorldTransform().m10(), two.getWorldTransform().m11(), two.getWorldTransform().m12());
        if (!overlapOnAxis(one, two, axis))
            return;

        one.getWorldTransform().getColumn(1, axis).cross(two.getWorldTransform().m20(), two.getWorldTransform().m21(), two.getWorldTransform().m22());
        if (!overlapOnAxis(one, two, axis))
            return;
        one.getWorldTransform().getColumn(2, axis).cross(two.getWorldTransform().m00(), two.getWorldTransform().m01(), two.getWorldTransform().m02());
        if (!overlapOnAxis(one, two, axis))
            return;

        one.getWorldTransform().getColumn(2, axis).cross(two.getWorldTransform().m10(), two.getWorldTransform().m11(), two.getWorldTransform().m12());
        if (!overlapOnAxis(one, two, axis))
            return;

        one.getWorldTransform().getColumn(2, axis).cross(two.getWorldTransform().m20(), two.getWorldTransform().m21(), two.getWorldTransform().m22());
        if (!overlapOnAxis(one, two, axis))
            return;


    }
}
