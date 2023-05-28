package com.example.demo.base.service.connection;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.AbstractPowerNode;
import com.example.demo.base.model.power.AbstractLine;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.status.BaseStatus;
import com.example.demo.base.service.element.AbstractElementService;
import com.example.demo.base.service.element.ElementService;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

@RequiredArgsConstructor
public abstract class AbstractConnectionService<PNODE extends AbstractPowerNode<? extends BaseStatus, ? extends BaseConnection>, LINE extends AbstractLine<PNODE>, ELEMENTSERVICE extends AbstractElementService<PNODE, LINE>> implements ConnectionService<PNODE> {

    protected final ElementService<PNODE, LINE> elementService;

    @Override
    public void connectNode(PNODE node) {
        List<PNODE> powerNodes = elementService.getMatrix().toOrderedNodeList();

        Collections.reverse(powerNodes);

        // Убираем саму node, чтобы не соединять её саму с собой
        powerNodes.remove(0);

        // Этот сэт для того, чтобы трансформаторы не были соединены друг с другом обоими обмотками
        Set<String> connectedNodes = new HashSet<>();

        node.getConnections().forEach((voltageLevel, connectionPoint) ->
                powerNodes.stream()
                    .filter(n -> !PowerNodeType.EMPTY.equals(n.getNodeType()))
                    .filter(n -> PowerNodeType.SUBSTATION.equals(node.getNodeType()) || PowerNodeType.SUBSTATION.equals(n.getNodeType()))
                    .filter(n -> !connectedNodes.contains(n.getUuid()))
                    .filter(n -> !n.getUuid().equals(node.getUuid()))
                    .filter(n -> sqrt(
                        pow(node.getX() - n.getX(), 2) + pow(node.getY() - n.getY(), 2)) <= 1.3 * voltageLevel.getBoundingArea()) // TODO Определиться с тем, насколько длинными могут быть линии
                    .filter(n -> n.getConnections().containsKey(voltageLevel))
//                    .filter(n -> n.getConnectionPoints().get(voltageLevel).getLimit() > n.getConnectionPoints().get(voltageLevel).getConnections()) // TODO при добавлении лимитов
//                    .limit(connectionPoint.getLimit() - connectionPoint.getConnections()) // TODO при добавлении лимитов
                    .forEach(n -> {
                        connectedNodes.add(n.getUuid());

                        //todo добавить логику определения breaker-а

                        connectNodes(n, node, voltageLevel, false);
                    })
        );
    }

    @Override
    public void connectNodes(PNODE node1, PNODE node2, VoltageLevel voltageLevel, boolean breaker) {
        LINE baseLine = getLine(node1, node2, voltageLevel, breaker); //todo добавить логику breaker-а

        elementService.addEdge(1);
        elementService.getLines().add(baseLine);
        node1.getConnections().get(voltageLevel).addConnection();
        node2.getConnections().get(voltageLevel).addConnection();

    }

    protected abstract LINE getLine(PNODE node1, PNODE node2, VoltageLevel voltageLevel, boolean breaker);

}
