package me.marlon.gfx;

public class Mesh implements AutoCloseable {
    private Primitive[] primitives;

    public Mesh(Primitive primitive) {
        this.primitives = new Primitive[1];
        this.primitives[0] = primitive;
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
