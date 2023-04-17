package com.example.demo.base.service;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.BaseLine;
import com.example.demo.base.model.power.BasePowerNode;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

@RequiredArgsConstructor
public class BaseConnectionService implements ConnectionService {

    private final BaseElementService elementService;

    @Override
    public void connectNode(BasePowerNode node, Matrix<? extends BasePowerNode> matrix) {
        List<? extends BasePowerNode> powerNodes = matrix.toOrderedNodeList();

        Collections.reverse(powerNodes);

        // Убираем саму ноду node, чтобы не соединять её саму с собой
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

                            BaseLine baseLine = new BaseLine(n, node, voltageLevel);
                            elementService.getLines().add(baseLine);
                            n.getConnections().get(voltageLevel).addConnection();
                            node.getConnections().get(voltageLevel).addConnection();

//                            Platform.runLater(() -> elementService.connectTwoNodes(
//                                n, n.getConnectionPoints().get(voltageLevel),
//                                node, node.getConnectionPoints().get(voltageLevel),
//                                voltageLevel
//                            ));
                        }
                    )
        );
    }

    @Override
    public void connectNodes(BasePowerNode node1, BasePowerNode node2, VoltageLevel voltageLevel) {
        BaseLine baseLine = new BaseLine(node1, node2, voltageLevel);
        elementService.getLines().add(baseLine);
        node1.getConnections().get(voltageLevel).addConnection();
        node2.getConnections().get(voltageLevel).addConnection();
    }
}
