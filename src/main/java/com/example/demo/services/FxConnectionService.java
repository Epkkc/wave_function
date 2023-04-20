package com.example.demo.services;

import com.example.demo.base.service.ConnectionService;
import com.example.demo.java.fx.model.power.FxPowerNode;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import javafx.application.Platform;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

@RequiredArgsConstructor
public class FxConnectionService {

    private final FxElementService elementService;

    public void connectNode(FxPowerNode node) {
        List<FxPowerNode> powerNodes = elementService.getMatrix().toOrderedNodeList();

        Collections.reverse(powerNodes);

        // Убираем саму ноду node, чтобы не соединять её саму с собой
        powerNodes.remove(0);

        // Этот сэт для того, чтобы трансформаторы не были соединены друг с другом обоими обмотками
        Set<String> connectedNodes = new HashSet<>();

        node.getConnectionPoints().forEach((voltageLevel, connectionPoint) ->
            powerNodes.stream()
                .filter(n -> !PowerNodeType.EMPTY.equals(n.getNodeType()))
                .filter(n -> PowerNodeType.SUBSTATION.equals(node.getNodeType()) || PowerNodeType.SUBSTATION.equals(n.getNodeType()))
                .filter(n -> !connectedNodes.contains(n.getUuid()))
                .filter(n -> !n.getUuid().equals(node.getUuid()))
                .filter(n ->
//                    (n.getX() <= node.getX() ||
//                    n.getY() <= node.getY()) &&
                    // TODO Определиться с тем, насколько длинными могут быть линии
                    sqrt(pow(node.getX() - n.getX(), 2) + pow(node.getY() - n.getY(), 2)) <= 1.3 * voltageLevel.getBoundingArea()
                )
                .filter(n -> n.getConnectionPoints().containsKey(voltageLevel))
//                .filter(n -> n.getConnectionPoints().get(voltageLevel).getLimit() > n.getConnectionPoints().get(voltageLevel).getConnections())
                .limit(connectionPoint.getLimit() - connectionPoint.getConnections())
                .forEach(n -> {
                        connectedNodes.add(n.getUuid());
                        Platform.runLater(() -> elementService.connectTwoNodes(
                            n, n.getConnectionPoints().get(voltageLevel),
                            node, node.getConnectionPoints().get(voltageLevel),
                            voltageLevel
                        ));
                    }
                )
        );
    }

    public void connectNodes(FxPowerNode node1, FxPowerNode node2, VoltageLevel voltageLevel) {
        Platform.runLater(() -> elementService.connectTwoNodes(
            node1, node1.getConnectionPoints().get(voltageLevel),
            node2, node2.getConnectionPoints().get(voltageLevel),
            voltageLevel
        ));
    }
}