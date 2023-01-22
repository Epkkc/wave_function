package com.example.demo;

import javafx.scene.Node;
import javafx.scene.shape.Shape;

public class NodeMeta {

    private final Shape shape;
    private final int x;
    private final int y;

    private SurfaceType surface;

    @Override
    public String toString() {
        return "NodeMeta{" +
            "node=" + shape +
            ", x=" + x +
            ", y=" + y +
            '}';
    }

    public NodeMeta(Shape shape, int x, int y) {
        this.shape = shape;
        this.x = x;
        this.y = y;
    }

    public Shape getShape() {
        return shape;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public SurfaceType getSurface() {
        return surface;
    }

    public void setSurface(SurfaceType surface) {
        this.surface = surface;
    }
}
