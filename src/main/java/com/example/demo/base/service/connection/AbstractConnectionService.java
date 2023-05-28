package com.example.demo.base.service.connection;

import com.example.demo.base.model.configuration.GeneratorConfiguration;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.TransformerConfiguration;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.power.AbstractPowerNode;
import com.example.demo.base.model.power.AbstractLine;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.status.BaseStatus;
import com.example.demo.base.service.BaseConfiguration;
import com.example.demo.base.service.element.AbstractElementService;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

@RequiredArgsConstructor
public abstract class AbstractConnectionService<PNODE extends AbstractPowerNode<? extends BaseStatus, ? extends BaseConnection>, LINE extends AbstractLine<PNODE>, ELEMENTSERVICE extends AbstractElementService<PNODE, LINE>> implements ConnectionService<PNODE> {

    protected final ELEMENTSERVICE elementService;
    protected final BaseConfiguration baseConfiguration;

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
                    .filter(n -> nodeTypeMatchCondition(node, n))
                    .filter(n -> !connectedNodes.contains(n.getUuid()))
                    .filter(n -> !n.getUuid().equals(node.getUuid()))
                    .filter(n -> sqrt(
                        pow(node.getX() - n.getX(), 2) + pow(node.getY() - n.getY(), 2)) <= getMaxLineLength(node, voltageLevel))
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


    protected double getMaxLineLength(PNODE node, VoltageLevel voltageLevel) {
        switch (node.getNodeType()) {
            case SUBSTATION -> {
                return baseConfiguration.getTransformerConfigurations().stream().filter(cfg -> cfg.getLevel().equals(voltageLevel)).findFirst().map(TransformerConfiguration::getMaxLineLength).orElseThrow(() -> new UnsupportedOperationException("There is no transformer configuration with voltage level " + voltageLevel));
            }
            case LOAD -> {
                return baseConfiguration.getLoadConfigurations().stream().filter(cfg -> cfg.getLevel().equals(voltageLevel)).findFirst().map(LoadConfiguration::getMaxLineLength).orElseThrow(() -> new UnsupportedOperationException("There is no transformer configuration with voltage level " + voltageLevel));
            }
            case GENERATOR -> {
                return baseConfiguration.getGeneratorConfigurations().stream().filter(cfg -> cfg.getLevel().equals(voltageLevel)).findFirst().map(GeneratorConfiguration::getMaxLineLength).orElseThrow(() -> new UnsupportedOperationException("There is no transformer configuration with voltage level " + voltageLevel)); // todo скорее всего переделать на boundingAreaTo
            }
            default -> {
                return 0;
            }
        }
    }

    protected boolean nodeTypeMatchCondition(PNODE mainNode, PNODE freeNode) {
        switch (mainNode.getNodeType()) {
            case SUBSTATION -> {
                return PowerNodeType.SUBSTATION.equals(freeNode.getNodeType()); // ПС соединяется только с ПС, поскольку на этапе расстановки ПС нет никаких альтернативных нод
            }
            case LOAD -> {
                return PowerNodeType.SUBSTATION.equals(freeNode.getNodeType()) || PowerNodeType.LOAD.equals(freeNode.getNodeType());  // Нагрузка соединяется с ПС и другими нагрузками для выстраивания древовидной структуры
            }
            case GENERATOR -> {
                return  PowerNodeType.SUBSTATION.equals(freeNode.getNodeType()); // Генератор соединяется только с ПС
            }
            default -> {
                return false;
            }
        }
    }
}
