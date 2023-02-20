package com.example.demo.services;

import com.example.demo.model.power.node.PowerNode;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

@RequiredArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {

    private final ElementServiceImpl elementService;

    @Override
    public void connectNode(PowerNode node) {
        List<PowerNode> powerNodes = elementService.getMatrix().toOrderedNodeList();

        Collections.reverse(powerNodes);

        //TODO убираем саму ноду node
        powerNodes.remove(0);

        node.getConnectionPoints().forEach((voltageLevel, connectionPoint) ->
            powerNodes.stream()
                .filter(n -> n.getNodeType() != null)
                .filter(n -> (n.getX() <= node.getX()
                    || n.getY() <= node.getY())
                    && sqrt(pow(node.getX() - n.getX(), 2) + pow(node.getY() - n.getY(), 2)) <= 3 * voltageLevel.getBoundingArea()
                    && n.getX() != node.getX()
                    && n.getY() != node.getY()
                )
                .filter(n -> n.getConnectionPoints().containsKey(voltageLevel))
                .limit(2) // TODO Заменить эту логику на максимальное количество выходящих из ноды линий
                .forEach(n -> elementService.connectTwoNodes(
                        n, n.getConnectionPoints().get(voltageLevel),
                        node, node.getConnectionPoints().get(voltageLevel),
                        voltageLevel
                    )
                )
        );
    }
}
