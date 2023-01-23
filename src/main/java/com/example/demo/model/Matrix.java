package com.example.demo.model;

import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Matrix<T extends Node> {

    private final List<List<NodeMeta<T>>> matrix;

    private final int x;
    private final int y;

    public Matrix(int range) {
        this.matrix = new ArrayList<>(range);
        for (int i = 0; i < range; i++) {
            matrix.add(new ArrayList<>(range));
        }
        this.x = range;
        this.y = range;
    }

    public Matrix(int x, int y) {
        this.matrix = new ArrayList<>();
        for (int i = 0; i < x; i++) {
            matrix.add(new ArrayList<>());
        }
        this.x = x;
        this.y = y;
    }

    public void add(NodeMeta<T> nodeMeta) {
        matrix.get(nodeMeta.getX()).add(nodeMeta.getY(), nodeMeta);
    }

    public Optional<NodeMeta<T>> getNodeMeta(int x, int y) {
        if (x < 0 || y < 0 || x >= this.x || y >= this.y) return Optional.empty();
        return Optional.ofNullable(matrix.get(x)).map(l -> l.get(y));
    }

    public Optional<Node> getNode(int x, int y) {
        return Optional.ofNullable(matrix.get(x).get(y).getNode());
    }

    public List<NodeMeta<T>> toNodeMetaList() {
        List<NodeMeta<T>> nodeMetas = new ArrayList<>(x * y);
        for (List<NodeMeta<T>> metas : matrix) {
            nodeMetas.addAll(metas);
        }
        return nodeMetas;
    }

    public List<Node> toNodeList() {
        return toNodeMetaList().stream().map(NodeMeta::getNode).collect(Collectors.toList());
    }

    public Optional<NodeMeta<T>> getTopNode(int x, int y) {
        if (x <= 0) return Optional.empty();
        return getNodeMeta(x - 1, y);
    }

    public Optional<NodeMeta<T>> getTopNode(NodeMeta<T> meta) {
        return getTopNode(meta.getX(), meta.getY());
    }

    public Optional<NodeMeta<T>> getLeftNode(int x, int y) {
        if (y <= 0) return Optional.empty();
        return getNodeMeta(x, y - 1);
    }

    public Optional<NodeMeta<T>> getLeftNode(NodeMeta<T> meta) {
        return getLeftNode(meta.getX(), meta.getY());
    }

    public Optional<NodeMeta<T>> getBottomNode(int x, int y) {
        return getNodeMeta(x - 1, y);
    }

    public Optional<NodeMeta<T>> getBottomNode(NodeMeta<T> meta) {
        return getBottomNode(meta.getX(), meta.getY());
    }

    public Optional<NodeMeta<T>> getRightNode(int x, int y) {
        return getNodeMeta(x, y + 1);
    }

    public Optional<NodeMeta<T>> getRightNode(NodeMeta<T> meta) {
        return getRightNode(meta.getX(), meta.getY());
    }

    public List<Optional<NodeMeta<T>>> getArea(int x, int y) {
        return List.of(getTopNode(x, y), getLeftNode(x, y), getBottomNode(x, y), getRightNode(x, y));
    }

    public List<Optional<NodeMeta<T>>> getArea(NodeMeta<T> meta) {
        return getArea(meta.getX(), meta.getY());
    }
}
