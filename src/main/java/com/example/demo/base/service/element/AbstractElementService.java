package com.example.demo.base.service.element;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.AbstractBasePowerNode;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.power.BaseLine;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class AbstractElementService<T extends AbstractBasePowerNode<? extends BaseConnection>> {

    protected final Matrix<T> matrix;
    protected final List<BaseLine> lines = new ArrayList<>();
    protected int sumLoad;
    protected int sumPower;
    protected int totalNumberOfNodes;
    protected int totalNumberOfEdges;

    public void addPowerNodeToGrid(T node) {
        matrix.add(node);
        if (PowerNodeType.LOAD.equals(node.getNodeType())) {
            sumLoad+=node.getPower();
        }
        if (PowerNodeType.GENERATOR.equals(node.getNodeType())) {
            sumPower +=node.getPower();
        }
    }

    public void addEdge(int value) {
        totalNumberOfEdges+=value;
    }

    public void addNode(int value) {
        totalNumberOfNodes++;
    }
}
