package me.marlon.physics;

import org.joml.Vector3f;

import java.util.List;

public class CollisionDetector {
    private static final int[] BOX_EDGES = {
            0, 1,
            0, 2,
            0, 4,
            1, 3,
            1, 5,
            2, 3,
            2, 6,
            3, 7,
            4, 5,
            4, 6,
            5, 7,
            6, 7
    };

    public static void collide(CollisionSphere one, CollisionSphere two, List<Contact> contacts) {
        Vector3f positionOne = one.getWorldTransform().getTranslation(new Vector3f());
        Vector3f positionTwo = two.getWorldTransform().getTranslation(new Vector3f());

        Vector3f midline = new Vector3f(positionOne).sub(positionTwo);
        float distance = midline.length();

        if (distance <= 0.0f || distance >= one.getRadius() + two.getRadius())
            return;

        positionOne.add(midline.x * 0.5f, midline.y * 0.5f, midline.z * 0.5f);
        midline.div(distance);
        float depth = one.getRadius() + two.getRadius() - distance;

        contacts.add(new Contact(positionOne, midline, depth, one.getBody(), two.getBody()));
    }

    public static void collide(CollisionSphere sphere, CollisionPlane plane, List<Contact> contacts) {
        Vector3f position = sphere.getWorldTransform().getTranslation(new Vector3f());

        float centerDistance = plane.getNormal().dot(position) - plane.getOffset(); //surfaceDistance + sphere.getRadius();
        float surfaceDistance = centerDistance - sphere.getRadius();//plane.getNormal().dot(position) - sphere.getRadius() - plane.getOffset();
        if (surfaceDistance >= 0.0f)
            return;

        position.add(plane.getNormal().x * centerDistance, plane.getNormal().y * centerDistance, plane.getNormal().z * centerDistance);
        Vector3f normal = new Vector3f(plane.getNormal());
        float depth = -surfaceDistance;

        contacts.add(new Contact(position, normal, depth, sphere.getBody(), plane.getBody()));
    }

    public static void collide(CollisionPlane plane, CollisionSphere sphere, List<Contact> contacts) {
        collide(sphere, plane, contacts);
    }

    public static void collide(CollisionBox box, CollisionPlane plane, List<Contact> contacts) {
        Vector3f vertex = new Vector3f();
        float vertexDistance;

        vertex.set(box.getHalfExtents().x, box.getHalfExtents().y, box.getHalfExtents().z);
        box.getWorldTransform().transformPosition(vertex);
        vertexDistance = vertex.dot(plane.getNormal()) - plane.getOffset();

        if (vertexDistance <= 0.0f) {
            Vector3f point = new Vector3f(vertex);
            Vector3f normal = new Vector3f(plane.getNormal());
            float depth = -vertexDistance;
            contacts.add(new Contact(point, normal, depth, box.getBody(), plane.getBody()));
        }

        vertex.set(box.getHalfExtents().x, box.getHalfExtents().y, -box.getHalfExtents().z);
        box.getWorldTransform().transformPosition(vertex);
        vertexDistance = vertex.dot(plane.getNormal()) - plane.getOffset();

        if (vertexDistance <= 0.0f) {
            Vector3f point = new Vector3f(vertex);
            Vector3f normal = new Vector3f(plane.getNormal());
            float depth = -vertexDistance;
            contacts.add(new Contact(point, normal, depth, box.getBody(), plane.getBody()));
        }

        vertex.set(box.getHalfExtents().x, -box.getHalfExtents().y, box.getHalfExtents().z);
        box.getWorldTransform().transformPosition(vertex);
        vertexDistance = vertex.dot(plane.getNormal()) - plane.getOffset();

        if (vertexDistance <= 0.0f) {
            Vector3f point = new Vector3f(vertex);
            Vector3f normal = new Vector3f(plane.getNormal());
            float depth = -vertexDistance;
            contacts.add(new Contact(point, normal, depth, box.getBody(), plane.getBody()));
        }

        vertex.set(box.getHalfExtents().x, -box.getHalfExtents().y, -box.getHalfExtents().z);
        box.getWorldTransform().transformPosition(vertex);
        vertexDistance = vertex.dot(plane.getNormal()) - plane.getOffset();

        if (vertexDistance <= 0.0f) {
            Vector3f point = new Vector3f(vertex);
            Vector3f normal = new Vector3f(plane.getNormal());
            float depth = -vertexDistance;
            contacts.add(new Contact(point, normal, depth, box.getBody(), plane.getBody()));
        }

        vertex.set(-box.getHalfExtents().x, box.getHalfExtents().y, box.getHalfExtents().z);
        box.getWorldTransform().transformPosition(vertex);
        vertexDistance = vertex.dot(plane.getNormal()) - plane.getOffset();

        if (vertexDistance <= 0.0f) {
            Vector3f point = new Vector3f(vertex);
            Vector3f normal = new Vector3f(plane.getNormal());
            float depth = -vertexDistance;
            contacts.add(new Contact(point, normal, depth, box.getBody(), plane.getBody()));
        }

        vertex.set(-box.getHalfExtents().x, box.getHalfExtents().y, -box.getHalfExtents().z);
        box.getWorldTransform().transformPosition(vertex);
        vertexDistance = vertex.dot(plane.getNormal()) - plane.getOffset();

        if (vertexDistance <= 0.0f) {
            Vector3f point = new Vector3f(vertex);
            Vector3f normal = new Vector3f(plane.getNormal());
            float depth = -vertexDistance;
            contacts.add(new Contact(point, normal, depth, box.getBody(), plane.getBody()));
        }

        vertex.set(-box.getHalfExtents().x, -box.getHalfExtents().y, box.getHalfExtents().z);
        box.getWorldTransform().transformPosition(vertex);
        vertexDistance = vertex.dot(plane.getNormal()) - plane.getOffset();

        if (vertexDistance <= 0.0f) {
            Vector3f point = new Vector3f(vertex);
            Vector3f normal = new Vector3f(plane.getNormal());
            float depth = -vertexDistance;
            contacts.add(new Contact(point, normal, depth, box.getBody(), plane.getBody()));
        }

        vertex.set(-box.getHalfExtents().x, -box.getHalfExtents().y, -box.getHalfExtents().z);
        box.getWorldTransform().transformPosition(vertex);
        vertexDistance = vertex.dot(plane.getNormal()) - plane.getOffset();

        if (vertexDistance <= 0.0f) {
            Vector3f point = new Vector3f(vertex);
            Vector3f normal = new Vector3f(plane.getNormal());
            float depth = -vertexDistance;
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
        closest.x = Math.min(Math.max(relCenter.x, -box.getHalfExtents().x), box.getHalfExtents().x);
        closest.y = Math.min(Math.max(relCenter.y, -box.getHalfExtents().y), box.getHalfExtents().y);
        closest.z = Math.min(Math.max(relCenter.z, -box.getHalfExtents().z), box.getHalfExtents().z);

        float distance = closest.distanceSquared(relCenter);
        if (distance > sphere.getRadius() * sphere.getRadius())
            return;

        box.getWorldTransform().transformPosition(closest);

        Vector3f point = closest;
        Vector3f normal = new Vector3f(closest).sub(center).normalize();
        float depth = sphere.getRadius() - (float) Math.sqrt(distance);
        contacts.add(new Contact(point, normal, depth, box.getBody(), sphere.getBody()));
    }

    public static void collide(CollisionSphere sphere, CollisionBox box, List<Contact> contacts) {
        collide(box, sphere, contacts);
    }

    private static float sizeOnAxis(CollisionBox box, Vector3f axis) {
        return  box.getHalfExtents().x
                    * Math.abs(axis.x * box.getWorldTransform().m00() + axis.y * box.getWorldTransform().m01() + axis.z * box.getWorldTransform().m02()) +
                box.getHalfExtents().y
                    * Math.abs(axis.x * box.getWorldTransform().m10() + axis.y * box.getWorldTransform().m11() + axis.z * box.getWorldTransform().m12()) +
                box.getHalfExtents().z
                    * Math.abs(axis.x * box.getWorldTransform().m20() + axis.y * box.getWorldTransform().m21() + axis.z * box.getWorldTransform().m22());
    }

    private static float intersectOnAxis(CollisionBox one, CollisionBox two, Vector3f axis, Vector3f toCenter) {
        float oneProject = sizeOnAxis(one, axis);
        float twoProject = sizeOnAxis(two, axis);

        float distance = Math.abs(axis.dot(toCenter));
        return oneProject + twoProject - distance;
    }

//    private static Vector3f projectToSegment(Vector3f position, Vector3f a, Vector3f b) {
//        Vector3f ba = new Vector3f(b).sub(a);
//        float t = new Vector3f(position).sub(a).dot(ba) / ba.dot(ba);
//        return new Vector3f(a).lerp(b, Math.min(Math.max(t, 0.0f), 1.0f));
//    }

    private static Vector3f collide(Vector3f pOne, Vector3f dOne, float oneSize, Vector3f pTwo, Vector3f dTwo, float twoSize, boolean useOne) {
        float smOne = dOne.lengthSquared();
        float smTwo = dTwo.lengthSquared();
        float dpOneTwo = dTwo.dot(dOne);

        Vector3f toSt = new Vector3f(pOne).sub(pTwo);
        float dpStaOne = dOne.dot(toSt);
        float dpStaTwo = dTwo.dot(toSt);

        float denom = smOne * smTwo - dpOneTwo * dpOneTwo;

        if (Math.abs(denom) < 0.0001f)
            return useOne ? pOne : pTwo;

        float mua = (dpOneTwo * dpStaTwo - smTwo * dpStaOne) / denom;
        float mub = (smOne * dpStaTwo - dpOneTwo * dpStaOne) / denom;

        if (mua > oneSize || mua < -oneSize || mub > twoSize || mub < -twoSize)
            return useOne ? pOne : pOne;
        else {
            Vector3f cOne = new Vector3f(dOne).mul(mua).add(pOne);
            Vector3f cTwo = new Vector3f(dTwo).mul(mub).add(pTwo);

            return cOne.lerp(cTwo, 0.5f);
        }
    }

    public static void collide(CollisionBox one, CollisionBox two, List<Contact> contacts) {
        Vector3f[] axes = new Vector3f[15];

        axes[0] = one.getWorldTransform().getColumn(0, new Vector3f());
        axes[1] = one.getWorldTransform().getColumn(1, new Vector3f());
        axes[2] = one.getWorldTransform().getColumn(2, new Vector3f());

        axes[3] = two.getWorldTransform().getColumn(0, new Vector3f());
        axes[4] = two.getWorldTransform().getColumn(1, new Vector3f());
        axes[5] = two.getWorldTransform().getColumn(2, new Vector3f());

        axes[6] = new Vector3f(axes[0]).cross(axes[3]);
        axes[7] = new Vector3f(axes[0]).cross(axes[4]);
        axes[8] = new Vector3f(axes[0]).cross(axes[5]);

        axes[9] = new Vector3f(axes[1]).cross(axes[3]);
        axes[10] = new Vector3f(axes[1]).cross(axes[4]);
        axes[11] = new Vector3f(axes[1]).cross(axes[5]);

        axes[12] = new Vector3f(axes[2]).cross(axes[3]);
        axes[13] = new Vector3f(axes[2]).cross(axes[4]);
        axes[14] = new Vector3f(axes[2]).cross(axes[5]);

        Vector3f toCenter = new Vector3f(two.getWorldTransform().m30() - one.getWorldTransform().m30(),
                                             two.getWorldTransform().m31() - one.getWorldTransform().m31(),
                                             two.getWorldTransform().m32() - one.getWorldTransform().m32());

        float bestOverlap = Float.MAX_VALUE;
        int bestCase = -1;

        for (int i = 0; i < 6; ++i) {
            Vector3f axis = axes[i];
            axis.normalize();

            float overlap = intersectOnAxis(one, two, axis, toCenter);
            if (overlap < 0.0f)
                return;

            if (overlap < bestOverlap) {
                bestOverlap = overlap;
                bestCase = i;
            }
        }

        int bestSingleAxis = bestCase;

        for (int i = 6; i < 15; ++i) {
            Vector3f axis = axes[i];

            if (axis.lengthSquared() < 0.001f)
                continue;

            axis.normalize();

            float overlap = intersectOnAxis(one, two, axis, toCenter);
            if (overlap < 0.0f)
                return;

            if (overlap < bestOverlap) {
                bestOverlap = overlap;
                bestCase = i;
            }
        }

        if (bestCase == -1)
            return;

        if (bestCase < 3) { // vertex of box two on face of box one
            Vector3f normal = axes[bestCase];

            if (normal.dot(toCenter) > 0.0f)
                normal.negate();

            Vector3f vertex = new Vector3f(two.getHalfExtents());
            if (axes[3].dot(normal) < 0.0f)
                vertex.x = -vertex.x;
            if (axes[4].dot(normal) < 0.0f)
                vertex.y = -vertex.y;
            if (axes[5].dot(normal) < 0.0f)
                vertex.z = -vertex.z;

            two.getWorldTransform().transformPosition(vertex);
            contacts.add(new Contact(vertex, normal, bestOverlap, one.getBody(), two.getBody()));
        } else if (bestCase < 6) { // vertex of box one on face of box two
            Vector3f normal = axes[bestCase];
            toCenter.negate();

            if (normal.dot(toCenter) > 0.0f)
                normal.negate();

            Vector3f vertex = new Vector3f(one.getHalfExtents());
            if (axes[0].dot(normal) < 0.0f)
                vertex.x = -vertex.x;
            if (axes[1].dot(normal) < 0.0f)
                vertex.y = -vertex.y;
            if (axes[2].dot(normal) < 0.0f)
                vertex.z = -vertex.z;

            one.getWorldTransform().transformPosition(vertex);
            contacts.add(new Contact(vertex, normal, bestOverlap, two.getBody(), one.getBody()));
        } else {
            bestCase -= 6;
            int oneAxisIndex = bestCase / 3;
            int twoAxisIndex = bestCase % 3;
            Vector3f oneAxis = axes[oneAxisIndex];
            Vector3f twoAxis = axes[twoAxisIndex + 3];
            Vector3f axis = new Vector3f(oneAxis).cross(twoAxis).normalize();

            if (axis.dot(toCenter) > 0.0f)
                axis.negate();

            Vector3f onOneEdge = new Vector3f(one.getHalfExtents());
            Vector3f onTwoEdge = new Vector3f(two.getHalfExtents());

            if (oneAxisIndex == 0)
                onOneEdge.x = 0.0f;
            else if (axes[0].dot(axis) > 0.0f)
                onOneEdge.x = -onOneEdge.x;

            if (twoAxisIndex == 0)
                onTwoEdge.x = 0.0f;
            else if (axes[3].dot(axis) < 0.0f)
                onTwoEdge.x = -onTwoEdge.x;

            if (oneAxisIndex == 1)
                onOneEdge.y = 0.0f;
            else if (axes[1].dot(axis) > 0.0f)
                onOneEdge.y = -onOneEdge.y;

            if (twoAxisIndex == 1)
                onTwoEdge.y = 0.0f;
            else if (axes[4].dot(axis) < 0.0f)
                onTwoEdge.y = -onTwoEdge.y;

            if (oneAxisIndex == 2)
                onOneEdge.z = 0.0f;
            else if (axes[2].dot(axis) > 0.0f)
                onOneEdge.z = -onOneEdge.z;

            if (twoAxisIndex == 2)
                onTwoEdge.z = 0.0f;
            else if (axes[5].dot(axis) < 0.0f)
                onTwoEdge.z = -onTwoEdge.z;

            one.getWorldTransform().transformPosition(onOneEdge);
            one.getWorldTransform().transformPosition(onTwoEdge);

            float oneSize;
            if (oneAxisIndex == 0)
                oneSize = one.getHalfExtents().x;
            else if (oneAxisIndex == 1)
                oneSize = one.getHalfExtents().y;
            else
                oneSize = one.getHalfExtents().z;

            float twoSize;
            if (twoAxisIndex == 0)
                twoSize = two.getHalfExtents().x;
            else if (twoAxisIndex == 1)
                twoSize = two.getHalfExtents().y;
            else
                twoSize = two.getHalfExtents().z;

            Vector3f vertex = collide(onOneEdge, oneAxis, oneSize, onTwoEdge, twoAxis, twoSize, bestSingleAxis > 2);
            contacts.add(new Contact(vertex, axis, bestOverlap, one.getBody(), two.getBody()));
        }
    }
}
