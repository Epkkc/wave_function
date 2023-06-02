package com.example.demo.base.service.element;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.AbstractLine;
import com.example.demo.base.model.power.AbstractPowerNode;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.power.NodeLineDto;
import com.example.demo.base.model.status.BaseStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public abstract class AbstractElementService<PNODE extends AbstractPowerNode<? extends BaseStatus, ? extends BaseConnection>, LINE extends AbstractLine<PNODE>> implements ElementService<PNODE, LINE> {

    protected final Matrix<PNODE> matrix;
    protected final Map<String, PNODE> uuidToNodeMap = new HashMap<>();
    protected final Map<String, LINE> uuidToLineMap = new HashMap<>();
    protected int sumLoad;
    protected int sumPower;

    @Override
    public void addPowerNodeToGrid(PNODE node) {
        matrix.add(node);
        uuidToNodeMap.merge(node.getUuid(), node, (n1, n2) -> {
            throw new UnsupportedOperationException(String.format("There is two nodes with equal uuid\nnode1=%s\nnoe2=%s", n1, n2));
        });
        if (PowerNodeType.LOAD.equals(node.getNodeType())) {
            sumLoad += node.getPower();
        }
        if (PowerNodeType.GENERATOR.equals(node.getNodeType())) {
            sumPower += node.getPower();
        }
    }

    @Override
    public void addLine(LINE line) {
        uuidToLineMap.put(line.getUuid(), line);
    }

    @Override
    public void removeLine(LINE line, boolean fromRemoveNodeMethod) {
        PNODE point1 = line.getPoint1();
        PNODE point2 = line.getPoint2();

        point1.getConnections().get(line.getVoltageLevel()).removeConnection(point2.getUuid(), line.getUuid());
        point2.getConnections().get(line.getVoltageLevel()).removeConnection(point1.getUuid(), line.getUuid());

        uuidToLineMap.remove(line.getUuid());
    }

    @Override
    public void removeLine(LINE line) {
        removeLine(line, false);
    }

    @Override
    public void removeNode(PNODE node, PNODE replaceNode) {
        matrix.add(replaceNode);
        uuidToNodeMap.remove(node.getUuid());

        List<LINE> linesForRemove = node.getConnections().values().stream()
            .map(BaseConnection::getNodeLineDtos)
            .flatMap(List::stream)
            .map(NodeLineDto::getLineUuid)
            .map(this::getLine)
            .map(opt -> opt.orElseThrow(() -> new UnsupportedOperationException("Unable to find one of lines for node : " + node)))
            .toList();

        beforeRemovingLines(linesForRemove);

        System.out.println("Removing excess load lines : " + linesForRemove);
        linesForRemove.forEach(line -> removeLine(line, true));
    }

    protected abstract void beforeRemovingLines(List<LINE> linesForRemove);

    @Override
    public Optional<LINE> getLine(String uuid) {
        return Optional.ofNullable(uuidToLineMap.get(uuid));
    }

    @Override
    public int getTotalNumberOfNodes() {
        return uuidToNodeMap.size();
    }

    @Override
    public int getTotalNumberOfEdges() {
        return uuidToLineMap.size();
    }

    @Override
    public PNODE getNodeByUuid(String uuid) {
        return uuidToNodeMap.get(uuid);
    }

    @Override
    public List<LINE> getLines() {
        return uuidToLineMap.values().stream().collect(Collectors.toList());
    }
}
