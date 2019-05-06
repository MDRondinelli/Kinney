package me.marlon.gfx;

import static org.lwjgl.system.MemoryUtil.*;

import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Mesh implements AutoCloseable {
    private Primitive[] primitives;

    public Mesh(String path) throws IOException {
        primitives = new Primitive[1];

        BufferedReader in = null;

        ArrayList<Vector3f> vertices = new ArrayList<>();
        ArrayList<Vector3f> vertexData = new ArrayList<>();

        try {
            in = new BufferedReader(new FileReader(path));

            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("v")) {
                    String[] splitted = line.split(" ");
                    float x = Float.parseFloat(splitted[1]);
                    float y = Float.parseFloat(splitted[2]);
                    float z = Float.parseFloat(splitted[3]);
                    vertices.add(new Vector3f(x, y, z));
                } else if (line.startsWith("f")) {
                    String[] splitted = line.split(" ");
                    int v1 = Integer.parseInt(splitted[1].split("/")[0]);
                    int v2 = Integer.parseInt(splitted[2].split("/")[0]);
                    int v3 = Integer.parseInt(splitted[3].split("/")[0]);
                    Vector3f p1 = vertices.get(v1 - 1);
                    Vector3f p2 = vertices.get(v2 - 1);
                    Vector3f p3 = vertices.get(v3 - 1);
                    Vector3f n = p2.sub(p1, new Vector3f()).cross(p3.sub(p1, new Vector3f())).normalize();

                    vertexData.add(p1);
                    vertexData.add(n);
                    vertexData.add(p2);
                    vertexData.add(n);
                    vertexData.add(p3);
                    vertexData.add(n);
                }
            }
        } finally {
            if (in != null)
                in.close();
        }

        FloatBuffer buffer = memAllocFloat(vertexData.size() * 3);

        for (int i = 0; i < vertexData.size(); ++i)
            vertexData.get(i).get(i * 3, buffer);

        primitives[0] = new Primitive(buffer);
        memFree(buffer);
    }

    public Mesh(Primitive[] primitives) {
        this.primitives = primitives;
    }

    public void close() {
        for (Primitive primitive : primitives)
            primitive.close();
    }

    public Primitive[] getPrimitives() {
        return primitives;
    }
}
