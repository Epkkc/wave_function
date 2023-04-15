package com.example.demo.model;

import com.example.demo.model.power.node.Coordinates;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// TODO сделать Iterable
public class Matrix<T extends Coordinates> implements Iterable<T> {

    private final List<List<T>> matrix;
    private int rows;
    private int columns;

    public Matrix(int range) {
        this.matrix = new ArrayList<>(range);
        for (int i = 0; i < range; i++) {
            matrix.add(new ArrayList<>(range));
        }
        this.rows = range;
        this.columns = range;
    }

    public Matrix(int rows, int columns) {
        this.matrix = new ArrayList<>(rows);
        for (int i = 0; i < rows; i++) {
            matrix.add(new ArrayList<>(columns));
        }
        this.rows = rows;
        this.columns = columns;
    }

    public void add(T node) {
//        matrix.get(node.getX()).add(node.getY(), node);
        matrix.get(node.getX()).replaceAll(n -> {
            if (n.getX() == node.getX() && n.getY() == node.getY()) {
                return node;
            }
            return n;
        });
    }

    public void simpleAdd(T node) {
        matrix.get(node.getX()).add(node.getY(), node);
    }

    public void fill(T node) {
        matrix.get(node.getX()).add(node.getY(), node);

    }

    public Optional<T> getNode(int x, int y) {
        if (x < 0 || y < 0 || x >= this.rows || y >= this.columns) return Optional.empty();
        return Optional.ofNullable(matrix.get(x)).map(l -> l.get(y));
    }

    public List<T> toNodeList() {
        List<T> result = new ArrayList<>(rows * columns);
        for (List<T> nodes : matrix) {
            result.addAll(nodes);
        }
        return result;
    }

    public List<T> toOrderedNodeList() {
        List<T> nodes = new ArrayList<>(rows * columns);

        int max = Math.max(rows, columns);
        for (int i = 0; i < max; i++) {
            for (int j = 0; j <= Math.min(i, rows - 1); j++) {
                if (i < columns) {
                    getNode(j, i).ifPresent(nodes::add);
//                    nodes.add(matrix.get(j).get(i));
                }
            }
            for (int j = Math.min(i, columns) - 1; j >= 0; j--) {
                if (i < rows) {
                    getNode(i, j).ifPresent(nodes::add);
//                    nodes.add(matrix.get(i).get(j));
                }
            }
        }

        return nodes;
    }

    public Optional<T> getTopNode(int x, int y) {
        if (x <= 0) return Optional.empty();
        return getNode(x - 1, y);
    }

    public Optional<T> getLeftNode(int x, int y) {
        if (y <= 0) return Optional.empty();
        return getNode(x, y - 1);
    }

    public Optional<T> getBottomNode(int x, int y) {
        return getNode(x - 1, y);
    }

    public Optional<T> getRightNode(int x, int y) {
        return getNode(x, y + 1);
    }

    public List<T> getArea(int row, int column) {
        return getArea(row, column, 1);
    }

    public List<T> getArea(int row, int column, int radius) {
        List<T> result = new ArrayList<>();

        for (int x = row-radius; x <= (row + radius); x++) {
            for (int y = column-radius; y <= (column + radius); y++) {
                if (x == row && y == column) continue;
                getNode(x, y).ifPresent(result::add);
            }
        }

        return result;
    }

    public void remove(int x, int y) {
        matrix.get(x).remove(y);
    }

    public void addRow() {
        matrix.add(new ArrayList<>(columns));
        rows++;
    }

    public void addColumn() {
        matrix.add(new ArrayList<>(columns));
        columns++;
    }

    public Optional<T> get(Predicate<T> predicate) {
        return toNodeList().stream().filter(predicate).findFirst();
    }

    public List<T> getAll(Predicate<T> predicate) {
        return toNodeList().stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public Iterator<T> iterator() {
        return toOrderedNodeList().iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return Iterable.super.spliterator();
    }
}
