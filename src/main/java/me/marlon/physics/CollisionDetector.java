package me.marlon.physics;

import me.marlon.gfx.TerrainMesh;
import org.joml.Vector3f;

import java.util.ArrayList;
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
            two.getWorldTransform().transformPosition(onTwoEdge);

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

    private static Vector3f closestPoint(Vector3f[] triangle, Vector3f point) {
        Vector3f edge0 = new Vector3f(triangle[1]).sub(triangle[0]);
        Vector3f edge1 = new Vector3f(triangle[2]).sub(triangle[0]);
        Vector3f v0 = new Vector3f(triangle[0]).sub(point);

        float a = edge0.dot(edge0);
        float b = edge0.dot(edge1);
        float c = edge1.dot(edge1);
        float d = edge0.dot(v0);
        float e = edge1.dot(v0);

        float det = a * c - b * b;
        float s = b * e - c * d;
        float t = b * d - a * e;

        if (s + t < det) {
            if (s < 0.0f) {
                if (t < 0.0f) {
                    if (d < 0.0f) {
                        s = Math.min(Math.max(-d / a, 0.0f), 1.0f);
                        t = 0.0f;
                    } else {
                        s = 0.0f;
                        t = Math.min(Math.max(-e / c, 0.0f), 1.0f);
                    }
                } else {
                    s = 0.0f;
                    t = Math.min(Math.max(-e / c, 0.0f), 1.0f);
                }
            } else if (t < 0.0f) {
                s = Math.min(Math.max(-d / a, 0.0f), 1.0f);
                t = 0.0f;
            } else {
                float invDet = 1.0f / det;
                s *= invDet;
                t *= invDet;
            }
        } else {
            if (s < 0.0f) {
                float tmp0 = b + d;
                float tmp1 = c + e;
                if (tmp1 > tmp0) {
                    float numer = tmp1 - tmp0;
                    float denom = a - 2 * b + c;
                    s = Math.min(Math.max(numer / denom, 0.0f), 1.0f);
                    t = 1.0f - s;
                } else {
                    s = 0.0f;
                    t = Math.min(Math.max(-e / c, 0.0f), 1.0f);
                }
            } else if (t < 0.0f) {
                if (a + d > b + e) {
                    float numer = c + e - b - d;
                    float denom = a - 2 * b + c;
                    s = Math.min(Math.max(numer / denom, 0.0f), 1.0f);
                    t = 1.0f - s;
                } else {
                    s = Math.min(Math.max(-e / c, 0.0f), 1.0f);
                    t = 0.0f;
                }
            } else {
                float numer = c + e - b - d;
                float denom = a - 2 * b + c;
                s = Math.min(Math.max(numer / denom, 0.0f), 1.0f);
                t = 1.0f - s;
            }
        }

        return edge0.mul(s).add(edge1.mul(t)).add(triangle[0]);
    }

    private static void collide(CollisionSphere sphere, CollisionPrimitive other, Vector3f[] triangle, List<Contact> contacts) {
        Vector3f center = sphere.getWorldTransform().getTranslation(new Vector3f());
        Vector3f point = closestPoint(triangle, center);

        if (center.distanceSquared(point) < sphere.getRadius() * sphere.getRadius()) {
            float depth = sphere.getRadius() - center.distance(point);
            Vector3f normal = center.sub(point).normalize();
            contacts.add(new Contact(point, normal, depth, sphere.getBody(), other.getBody()));
        }
    }

    public static void collide(CollisionSphere sphere, CollisionTerrain terrain, List<Contact> contacts) {
        Vector3f center = sphere.getWorldTransform().getTranslation(new Vector3f());
        float minX = (center.x - sphere.getRadius()) / TerrainMesh.TILE_SIZE;
        float maxX = (center.x + sphere.getRadius()) / TerrainMesh.TILE_SIZE;
        float minZ = (center.z - sphere.getRadius()) / TerrainMesh.TILE_SIZE;
        float maxZ = (center.z + sphere.getRadius()) / TerrainMesh.TILE_SIZE;

        int minI = (int) minX;
        if (minI < 0)
            minI = 0;
        if (minI > terrain.getSize() - 1)
            minI = terrain.getSize() - 1;

        int maxI = (int) (maxX + 1.0f);
        if (maxI < 0)
            maxI = 0;
        if (maxI > terrain.getSize() - 1)
            maxI = terrain.getSize() - 1;

        int minJ = (int) minZ;
        if (minJ < 0)
            minJ = 0;
        if (minJ > terrain.getSize() - 1)
            minJ = terrain.getSize() - 1;

        int maxJ = (int) (maxZ + 1.0f);
        if (maxJ < 0)
            maxJ = 0;
        if (maxJ > terrain.getSize() - 1)
            maxJ = terrain.getSize() - 1;

        Vector3f[] triangle = new Vector3f[3];

        for (int i = minI; i < maxI; ++i) {
            for (int j = minJ; j < maxJ; ++j) {
                for (int k = 0; k < 2; ++k) {
                    terrain.getTriangle(i, j, triangle, k);
                    collide(sphere, terrain, triangle, contacts);
                }
            }
        }
    }

    public static void collide(CollisionTerrain terrain, CollisionSphere sphere, List<Contact> contacts) {
        collide(sphere, terrain, contacts);
    }

    private static float intersectOnAxis(CollisionBox box, Vector3f[] boxSpaceTriangle, Vector3f axis) {
        Vector3f boxSpaceAxis = new Vector3f(axis).mulDirection(box.getWorldTransformInv());

        float p0 = boxSpaceAxis.dot(boxSpaceTriangle[0]);
        float p1 = boxSpaceAxis.dot(boxSpaceTriangle[1]);
        float p2 = boxSpaceAxis.dot(boxSpaceTriangle[2]);

        float pMin = Math.min(Math.min(p0, p1), p2);
        float pMax = Math.max(Math.max(p0, p1), p2);

        float boxSize = box.getHalfExtents().x * Math.abs(boxSpaceAxis.x) + box.getHalfExtents().y * Math.abs(boxSpaceAxis.y) + box.getHalfExtents().z * Math.abs(boxSpaceAxis.z);

        if (boxSize <= pMin)
            return boxSize - pMin;
        if (-boxSize >= pMax)
            return boxSize + pMax;
        if (-boxSize <= pMin && pMax <= boxSize)
            return Math.min(boxSize - pMin, boxSize + pMax);
        if (pMin <= -boxSize && boxSize <= pMax)
            return Math.min(boxSize - pMin, boxSize + pMax);
        if (pMin <= boxSize && boxSize <= pMax)
            return boxSize - pMin;
        if (pMin <= -boxSize && -boxSize <= pMax)
            return boxSize + pMax;

        return 0.0f;
    }

    private static void collide(CollisionBox box, CollisionPrimitive other, Vector3f[] triangle, List<Contact> contacts) {
        Vector3f[] edges = new Vector3f[3];
        edges[0] = new Vector3f(triangle[1]).sub(triangle[0]);
        edges[1] = new Vector3f(triangle[2]).sub(triangle[1]);
        edges[2] = new Vector3f(triangle[0]).sub(triangle[2]);

        Vector3f[] axes = new Vector3f[13];

        axes[0] = new Vector3f(edges[0]).cross(edges[1]);

        axes[1] = box.getWorldTransform().getColumn(0, new Vector3f());
        axes[2] = box.getWorldTransform().getColumn(1, new Vector3f());
        axes[3] = box.getWorldTransform().getColumn(2, new Vector3f());

        axes[4] = new Vector3f(axes[1]).cross(edges[0]);
        axes[5] = new Vector3f(axes[1]).cross(edges[1]);
        axes[6] = new Vector3f(axes[1]).cross(edges[2]);

        axes[7] = new Vector3f(axes[2]).cross(edges[0]);
        axes[8] = new Vector3f(axes[2]).cross(edges[1]);
        axes[9] = new Vector3f(axes[2]).cross(edges[2]);

        axes[10] = new Vector3f(axes[3]).cross(edges[0]);
        axes[11] = new Vector3f(axes[3]).cross(edges[1]);
        axes[12] = new Vector3f(axes[3]).cross(edges[2]);

        Vector3f[] boxSpaceTriangle = new Vector3f[3];
        boxSpaceTriangle[0] = new Vector3f(triangle[0]).mulPosition(box.getWorldTransformInv());
        boxSpaceTriangle[1] = new Vector3f(triangle[1]).mulPosition(box.getWorldTransformInv());
        boxSpaceTriangle[2] = new Vector3f(triangle[2]).mulPosition(box.getWorldTransformInv());

        float bestOverlap = Float.MAX_VALUE;
        int bestCase = -1;

        // handle axes[0] separately
//        {
//            Vector3f axis = axes[0];
//            axis.normalize();
//
//            Vector3f boxSpaceAxis = new Vector3f(axis).mulDirection(box.getWorldTransformInv());
//
//            float p = boxSpaceAxis.dot(boxSpaceTriangle[0]);
//            float boxSize = box.getHalfExtents().x * Math.abs(boxSpaceAxis.x) + box.getHalfExtents().y * Math.abs(boxSpaceAxis.y) + box.getHalfExtents().z * Math.abs(boxSpaceAxis.z);
//
//            float overlap = 0.0f;
//            if (boxSize <= p)
//                overlap = boxSize - p;
//            else if (-boxSize >= p)
//                overlap = boxSize + p;
//            else if (-boxSize <= p && p <= boxSize)
//                overlap = Math.min(boxSize - p, boxSize + p);
//
//            if (overlap < 0.0f)
//                return;
//
//            if (overlap < bestOverlap) {
//                bestOverlap = overlap;
//                bestCase = 0;
//            }
//        }

        for (int i = 0; i < 4; ++i) {
            Vector3f axis = axes[i];
            axis.normalize();

            float overlap = intersectOnAxis(box, boxSpaceTriangle, axis);
            if (overlap < 0.0f)
                return;

            if (overlap < bestOverlap) {
                bestOverlap = overlap;
                bestCase = i;
            }
        }

        int bestSingleAxis = bestCase;

        for (int i = 4; i < 13; ++i) {
            Vector3f axis = axes[i];

            if (axis.lengthSquared() < 0.001f)
                continue;

            axis.normalize();

            float overlap = intersectOnAxis(box, boxSpaceTriangle, axis);
            if (overlap < 0.0f)
                return;

            if (overlap < bestOverlap) {
                bestOverlap = overlap;
                bestCase = i;
            }
        }

        if (bestCase == -1)
            return;

        if (bestCase == 0) { // vertex of box on face of triangle
            Vector3f normal = axes[bestCase];

//            Vector3f toCenter = box.getWorldTransform().getTranslation(new Vector3f()).sub(
//                    (triangle[0].x + triangle[1].x + triangle[2].x) / 3.0f,
//                    (triangle[0].y + triangle[1].y + triangle[2].y) / 3.0f,
//                    (triangle[0].z + triangle[1].z + triangle[2].z) / 3.0f);
//
//            if (normal.dot(toCenter) < 0.0f)
//                normal.negate();

            Vector3f vertex = new Vector3f(box.getHalfExtents());
            if (axes[1].dot(normal) > 0.0f)
                vertex.x = -vertex.x;
            if (axes[2].dot(normal) > 0.0f)
                vertex.y = -vertex.y;
            if (axes[3].dot(normal) > 0.0f)
                vertex.z = -vertex.z;

            box.getWorldTransform().transformPosition(vertex);
            contacts.add(new Contact(vertex, normal, bestOverlap, box.getBody(), other.getBody()));
        } else if (bestCase < 4) { // vertex of triangle on face of box
            Vector3f normal = axes[bestCase]; // should already be normalized

            Vector3f toCenter = box.getWorldTransform().getTranslation(new Vector3f()).sub(
                    (triangle[0].x + triangle[1].x + triangle[2].x) / 3.0f,
                    (triangle[0].y + triangle[1].y + triangle[2].y) / 3.0f,
                    (triangle[0].z + triangle[1].z + triangle[2].z) / 3.0f);

            // flip normal away from triangle
            if (normal.dot(toCenter) < 0.0f)
                normal.negate(); // mutates axes[bestCase]

            float p0 = normal.dot(triangle[0]);
            float p1 = normal.dot(triangle[1]);
            float p2 = normal.dot(triangle[2]);

            if (p0 > p1) {
                if (p0 > p2)
                    contacts.add(new Contact(new Vector3f(triangle[0]), normal, bestOverlap, box.getBody(), other.getBody()));
                else
                    contacts.add(new Contact(new Vector3f(triangle[2]), normal, bestOverlap, box.getBody(), other.getBody()));
            } else {
                if (p1 > p2)
                    contacts.add(new Contact(new Vector3f(triangle[1]), normal, bestOverlap, box.getBody(), other.getBody()));
                else
                    contacts.add(new Contact(new Vector3f(triangle[2]), normal, bestOverlap, box.getBody(), other.getBody()));
            }
        } else { // edge-edge
            bestCase -= 4;
            int boxAxisIndex = bestCase / 3;
            int triEdgeIndex = bestCase % 3;

            Vector3f boxAxis = new Vector3f(axes[boxAxisIndex + 1]);
            Vector3f triAxis = new Vector3f(edges[triEdgeIndex]);
            Vector3f axis = new Vector3f(axes[bestCase + 4]);

            Vector3f toCenter = box.getWorldTransform().getTranslation(new Vector3f()).sub(
                    (triangle[0].x + triangle[1].x + triangle[2].x) / 3.0f,
                    (triangle[0].y + triangle[1].y + triangle[2].y) / 3.0f,
                    (triangle[0].z + triangle[1].z + triangle[2].z) / 3.0f);

            if (axis.dot(toCenter) < 0.0f)
                axis.negate();

            Vector3f pointOnBox = new Vector3f(box.getHalfExtents());

            if (boxAxisIndex == 0)
                pointOnBox.x = 0.0f;
            else if (axis.dot(axes[1]) > 0.0f)
                pointOnBox.x = -pointOnBox.x;

            if (boxAxisIndex == 1)
                pointOnBox.y = 0.0f;
            else if (axis.dot(axes[2]) > 0.0f)
                pointOnBox.y = -pointOnBox.y;

            if (boxAxisIndex == 2)
                pointOnBox.z = 0.0f;
            else if (axis.dot(axes[3]) > 0.0f)
                pointOnBox.z = -pointOnBox.z;

            pointOnBox.mulPosition(box.getWorldTransform());

            Vector3f pointOnTri = new Vector3f();
            if (triEdgeIndex == 0)
                pointOnTri.set(triangle[0]).lerp(triangle[1], 0.5f);
            else if (triEdgeIndex == 1)
                pointOnTri.set(triangle[1]).lerp(triangle[2], 0.5f);
            else
                pointOnTri.set(triangle[0]).lerp(triangle[2], 0.5f);

            float boxSize;
            if (boxAxisIndex == 0)
                boxSize = box.getHalfExtents().x;
            else if (boxAxisIndex == 1)
                boxSize = box.getHalfExtents().y;
            else
                boxSize = box.getHalfExtents().z;

            float triSize = triAxis.length();
            triAxis.div(triSize);
            triSize *= 0.5f;

            Vector3f vertex = collide(pointOnTri, triAxis, triSize, pointOnBox, boxAxis, boxSize, bestSingleAxis != 0);
            contacts.add(new Contact(vertex, axis, bestOverlap, box.getBody(), other.getBody()));
        }
    }

    public static void collide(CollisionBox box, CollisionTerrain terrain, List<Contact> contacts) {
        float minX = (box.getWorldAabb().minX) / TerrainMesh.TILE_SIZE;
        float maxX = (box.getWorldAabb().maxX) / TerrainMesh.TILE_SIZE;
        float minZ = (box.getWorldAabb().minZ) / TerrainMesh.TILE_SIZE;
        float maxZ = (box.getWorldAabb().maxZ) / TerrainMesh.TILE_SIZE;

        int minI = (int) minX;
        if (minI < 0)
            minI = 0;
        if (minI > terrain.getSize() - 1)
            minI = terrain.getSize() - 1;

        int maxI = (int) (maxX + 1.0f);
        if (maxI < 0)
            maxI = 0;
        if (maxI > terrain.getSize() - 1)
            maxI = terrain.getSize() - 1;

        int minJ = (int) minZ;
        if (minJ < 0)
            minJ = 0;
        if (minJ > terrain.getSize() - 1)
            minJ = terrain.getSize() - 1;

        int maxJ = (int) (maxZ + 1.0f);
        if (maxJ < 0)
            maxJ = 0;
        if (maxJ > terrain.getSize() - 1)
            maxJ = terrain.getSize() - 1;

//        List<Contact> contactList = new ArrayList<>();

        Vector3f[] triangle = new Vector3f[3];
        for (int i = minI; i < maxI; ++i) {
            for (int j = minJ; j < maxJ; ++j) {
                for (int k = 0; k < 2; ++k) {
                    terrain.getTriangle(i, j, triangle, k);
                    collide(box, terrain, triangle, contacts);
                }
            }
        }

//        Contact worst = null;
//        for (int i = 0; i < contactList.size(); ++i)
//            if (worst == null || contactList.get(i).getDepth() > worst.getDepth())
//                worst = contactList.get(i);
//
//        if (worst != null)
//            contacts.add(worst)
    }

    public static void collide(CollisionTerrain terrain, CollisionBox box, List<Contact> contacts) {
        collide(box, terrain, contacts);
    }
}
