package com.example.demo.base.service.element;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.AbstractPowerNode;
import com.example.demo.base.model.power.AbstractLine;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.status.BaseStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
@RequiredArgsConstructor
public abstract class AbstractElementService<PNODE extends AbstractPowerNode<? extends BaseStatus, ? extends BaseConnection>, LINE extends AbstractLine<PNODE>> implements ElementService<PNODE, LINE> {

    protected final Matrix<PNODE> matrix;
    protected final Map<String, PNODE> uuidToNodeMap = new HashMap<>();
    protected final List<LINE> lines = new ArrayList<>();
    protected int sumLoad;
    protected int sumPower;

    @Override
    public void addPowerNodeToGrid(PNODE node) {
        matrix.add(node);
        uuidToNodeMap.merge(node.getUuid(), node, (n1, n2) -> {
            throw new UnsupportedOperationException(String.format("There is two nodes with equal uuid\nnode1=%s\nnoe2=%s", n1, n2));
        } );
        if (PowerNodeType.LOAD.equals(node.getNodeType())) {
            sumLoad+=node.getPower();
        }
        if (PowerNodeType.GENERATOR.equals(node.getNodeType())) {
            sumPower +=node.getPower();
        }
    }

    @Override
    public void addLine(LINE line) {
        lines.add(line);
    }

    @Override
    public void removeLine(LINE line) {
        PNODE point1 = line.getPoint1();
        PNODE point2 = line.getPoint2();

        point1.getConnections().get(line.getVoltageLevel()).removeConnection(point2.getUuid());
        point2.getConnections().get(line.getVoltageLevel()).removeConnection(point1.getUuid());

        lines.remove(line);
    }

    @Override
    public Optional<LINE> getLine(String uuid) {
        return lines.stream()
            .filter(line -> line.getUuid().equals(uuid))
            .findFirst(); // todo переделать на hashMap
    }

    @Override
    public int getTotalNumberOfNodes() {
        return uuidToNodeMap.size();
    }

    @Override
    public int getTotalNumberOfEdges() {
        return lines.size();
    }

    @Override
    public PNODE getNodeByUuid(String uuid) {
        return uuidToNodeMap.get(uuid);
    }

}
