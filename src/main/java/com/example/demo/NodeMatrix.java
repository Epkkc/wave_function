package com.example.demo;

import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NodeMatrix {

    private List<List<NodeMeta>> matrix;

    private int x;
    private int y;

    public NodeMatrix(int range) {
        this.matrix = new ArrayList<>(range);
        for (int i = 0; i < range; i++) {
            matrix.add(new ArrayList<>(range));
        }
        this.x = range;
        this.y = range;
    }

    public NodeMatrix(int x, int y) {
        this.matrix = new ArrayList<>();
        for (int i = 0; i < x; i++) {
            matrix.add(new ArrayList<>());
        }
        this.x = x;
        this.y = y;
    }

    public void add(NodeMeta nodeMeta) {
        matrix.get(nodeMeta.getX()).add(nodeMeta.getY(), nodeMeta);
    }

    public Optional<NodeMeta> getNodeMeta(int x, int y) {
        if (x < 0 || y < 0 || x >= this.x || y >= this.y) return Optional.empty();
        return Optional.ofNullable(matrix.get(x)).map(l -> l.get(y));
    }

    public Optional<Node> getNode(int x, int y) {
        return Optional.ofNullable(matrix.get(x).get(y).getShape());
    }

    public List<NodeMeta> toNodeMetaList() {
        List<NodeMeta> nodeMetas = new ArrayList<>(x * y);
        for (List<NodeMeta> metas : matrix) {
            nodeMetas.addAll(metas);
        }
        return nodeMetas;
    }

    public List<Node> toNodeList() {
        return toNodeMetaList().stream().map(NodeMeta::getShape).collect(Collectors.toList());
    }

    public Optional<NodeMeta> getTopNode(int x, int y) {
        if (x <= 0) return Optional.empty();
        return getNodeMeta(x - 1, y);
    }

    public Optional<NodeMeta> getTopNode(NodeMeta meta) {
        return getTopNode(meta.getX(), meta.getY());
    }

    public Optional<NodeMeta> getLeftNode(int x, int y) {
        if (y <= 0) return Optional.empty();
        return getNodeMeta(x, y - 1);
    }

    public Optional<NodeMeta> getLeftNode(NodeMeta meta) {
        return getLeftNode(meta.getX(), meta.getY());
    }

    public Optional<NodeMeta> getBottomNode(int x, int y) {
        return getNodeMeta(x - 1, y);
    }

    public Optional<NodeMeta> getBottomNode(NodeMeta meta) {
        return getBottomNode(meta.getX(), meta.getY());
    }

    public Optional<NodeMeta> getRightNode(int x, int y) {
        return getNodeMeta(x, y + 1);
    }

    public Optional<NodeMeta> getRightNode(NodeMeta meta) {
        return getRightNode(meta.getX(), meta.getY());
    }

    public List<Optional<NodeMeta>> getArea(int x, int y) {
        return List.of(getTopNode(x, y), getLeftNode(x, y), getBottomNode(x, y), getRightNode(x, y));
    }

    public List<Optional<NodeMeta>> getArea(NodeMeta meta) {
        return getArea(meta.getX(), meta.getY());
    }
}
