package com.example.demo.base.service.element;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.AbstractBasePowerNode;
import com.example.demo.base.model.power.AbstractLine;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.status.BaseStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public abstract class AbstractElementService<PNODE extends AbstractBasePowerNode<? extends BaseStatus, ? extends BaseConnection>, LINE extends AbstractLine<PNODE>> implements ElementService<PNODE, LINE> {

    protected final Matrix<PNODE> matrix;
    protected final List<LINE> lines = new ArrayList<>();
    protected int sumLoad;
    protected int sumPower;
    protected int totalNumberOfNodes;
    protected int totalNumberOfEdges;

    public void addPowerNodeToGrid(PNODE node) {
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
        totalNumberOfNodes+=value;
    }
}
