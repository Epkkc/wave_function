package com.example.demo.model;

import javafx.scene.Node;

public class NodeMeta<T extends Node> {

    private final T node;
    private final int x;
    private final int y;

    private SurfaceType surface;

    @Override
    public String toString() {
        return "NodeMeta{" +
            "node=" + node +
            ", x=" + x +
            ", y=" + y +
            '}';
    }

    public NodeMeta(T node, int x, int y) {
        this.node = node;
        this.x = x;
        this.y = y;
    }

    public T getNode() {
        return node;
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
