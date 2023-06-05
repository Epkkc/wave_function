package com.example.demo.base.service.connection;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.power.AbstractLine;
import com.example.demo.base.model.power.AbstractPowerNode;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.power.NodeLineDto;
import com.example.demo.base.model.status.BaseStatus;
import com.example.demo.base.service.BaseConfiguration;
import com.example.demo.base.service.TopologyService;
import com.example.demo.base.service.element.ElementService;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

@RequiredArgsConstructor
public abstract class AbstractConnectionService<PNODE extends AbstractPowerNode<? extends BaseStatus, ? extends BaseConnection>, LINE extends AbstractLine<PNODE>, ELEMENTSERVICE extends ElementService<PNODE, LINE>> implements ConnectionService<PNODE> {

    protected final ELEMENTSERVICE elementService; // todo убрать потом параметризацию заменив на интерфейс
    protected final BaseConfiguration baseConfiguration;
    protected final TopologyService<PNODE, LINE> topologyService;

    @Override
    public void connectNode(PNODE node) {
        connectNode(node, Set.of());
    }

    @Override
    public void connectNode(PNODE node, Set<String> ignoreUuids) {
        if (ignoreUuids == null) {
            throw new IllegalArgumentException("Collection ignoreUuids = null");
        }

        List<PNODE> powerNodes = elementService.getMatrix().toOrderedNodeList();

        Collections.reverse(powerNodes);

        // Убираем саму node, чтобы не соединять её саму с собой
        powerNodes.remove(0); // todo удалить, потому что есть фильтр в стриме

        // Этот сэт для того, чтобы трансформаторы не были соединены друг с другом обоими обмотками
        Set<String> connectedNodes = new HashSet<>();

        node.getConnections().forEach((voltageLevel, connectionPoint) ->
                powerNodes.stream()
                    .filter(n -> !PowerNodeType.EMPTY.equals(n.getNodeType()))
                    .filter(n -> !ignoreUuids.contains(n.getUuid()))
                    .filter(n -> !connectedNodes.contains(n.getUuid()))
                    .filter(n -> !n.getUuid().equals(node.getUuid()))
                    .filter(n -> sqrt(
                        pow(node.getX() - n.getX(), 2) + pow(node.getY() - n.getY(), 2)) <= getMaxLineLength(node, voltageLevel))
                    .filter(n -> n.getConnections().containsKey(voltageLevel))
                    .filter(n -> nodeTypeMatchCondition(node, n))
//                    .filter(n -> n.getConnectionPoints().get(voltageLevel).getLimit() > n.getConnectionPoints().get(voltageLevel).getConnections()) // TODO при добавлении лимитов
//                    .limit(connectionPoint.getLimit() - connectionPoint.getConnections()) // TODO при добавлении лимитов
                    .forEach(n -> {
                        connectedNodes.add(n.getUuid());
                        connectNodes(n, node, voltageLevel);
                    })
        );
    }

    @Override
    public void connectNodes(PNODE node1, PNODE node2, VoltageLevel voltageLevel) {
        LINE baseLine = getLine(node1, node2, voltageLevel, getBreakerProperty(node1, node2));

        elementService.addLine(baseLine);
        node1.getConnections().get(voltageLevel).addConnection(node2.getUuid(), baseLine.getUuid());
        node2.getConnections().get(voltageLevel).addConnection(node1.getUuid(), baseLine.getUuid());
    }

    @Override
    public void connectNodes(PNODE node1, PNODE node2, VoltageLevel voltageLevel, boolean breaker) {
        LINE baseLine = getLine(node1, node2, voltageLevel, breaker);
        elementService.addLine(baseLine);
        node1.getConnections().get(voltageLevel).addConnection(node2.getUuid(), baseLine.getUuid());
        node2.getConnections().get(voltageLevel).addConnection(node1.getUuid(), baseLine.getUuid());
    }

    protected boolean getBreakerProperty(PNODE node1, PNODE node2) {
        // breaker устанавливался в следующих случаях:
        // 1) Когда соединяются две LOAD, принадлежащие к разным фидерам (эта логика реализована в методе nodeTypeMatchCondition -> loadsBelongDifferentFeeders)
        // 2) Когда LOAD с chainLinkNumber = 1 соединяется с другой SUBSTATION //todo это ещё уточняется
        return nodesAreLoads(node1, node2);

    }

    protected boolean nodesAreLoads(PNODE node1, PNODE node2) {
        return node1.getNodeType().equals(PowerNodeType.LOAD) && node2.getNodeType().equals(PowerNodeType.LOAD);
    }

    protected abstract LINE getLine(PNODE node1, PNODE node2, VoltageLevel voltageLevel, boolean breaker);


    protected double getMaxLineLength(PNODE node, VoltageLevel voltageLevel) {
        switch (node.getNodeType()) {
            case SUBSTATION -> {
                return baseConfiguration.getTransformerConfiguration(voltageLevel).getMaxLineLength();
            }
            case LOAD -> {
                return baseConfiguration.getLoadConfiguration(voltageLevel).getMaxLineLength();
            }
            case GENERATOR -> {
                return baseConfiguration.getGeneratorConfiguration(voltageLevel).getMaxLineLength();
            }
            default -> {
                return 0;
            }
        }
    }

    protected boolean nodeTypeMatchCondition(PNODE mainNode, PNODE freeNode) {
        switch (mainNode.getNodeType()) {
            case SUBSTATION, GENERATOR -> {
                return PowerNodeType.SUBSTATION.equals(freeNode.getNodeType()); // ПС соединяется только с ПС, поскольку на этапе расстановки ПС нет никаких альтернативных нод
            }
            case LOAD -> {
                return PowerNodeType.LOAD.equals(freeNode.getNodeType())
                    && loadsBelongDifferentFeeders(mainNode, freeNode)
                    && feedersAreNotConnected(mainNode, freeNode)
                    && limitCondition(mainNode, freeNode)
//                    || PowerNodeType.SUBSTATION.equals(freeNode.getNodeType()) // todo это уточняется
//                    && substationBelongDifferentFeeder(mainNode, freeNode)
                    ;
            }
            default -> {
                return false;
            }
        }
    }

    // Данные метод проверяет, что фидеры ещё не были друг с другом соединены
//    private boolean feedersAreNotConnected(PNODE mainNode, PNODE freeNode) {
//        List<String> connectedFeedersToMain = new ArrayList<>();
//        topologyService.getConnectedFeeders(mainNode, connectedFeedersToMain, new ArrayList<>());
//
//        List<String> connectedFeedersToFree = new ArrayList<>();
//        topologyService.getConnectedFeeders(freeNode, connectedFeedersToFree, new ArrayList<>());
//
//        PNODE sourceConnectedSubstationMain = topologyService.getSourceConnectedSubstation(mainNode)
//            .orElseThrow(() -> new UnsupportedOperationException("Unable to find SourceConnectedSubstation for node : " + mainNode));
//
//        PNODE sourceConnectedSubstationFree = topologyService.getSourceConnectedSubstation(freeNode)
//            .orElseThrow(() -> new UnsupportedOperationException("Unable to find SourceConnectedSubstation for node : " + freeNode));
//
//        return !connectedFeedersToMain.contains(sourceConnectedSubstationFree.getUuid()) && !connectedFeedersToFree.contains(sourceConnectedSubstationMain.getUuid());
//    }

    // Данный метод проверяет, что питающие ПС каждой нагрузки ещё не соединены
    private boolean feedersAreNotConnected(PNODE mainNode, PNODE freeNode) {
        PNODE sourceConnectedSubstationMain = topologyService.getSourceConnectedSubstation(mainNode)
            .orElseThrow(() -> new UnsupportedOperationException("Unable to find SourceConnectedSubstation for load : " + mainNode));
        PNODE sourceConnectedSubstationFree = topologyService.getSourceConnectedSubstation(freeNode)
            .orElseThrow(() -> new UnsupportedOperationException("Unable to find SourceConnectedSubstation for load : " + freeNode));

        List<String> connectedSubstationsViaFeedersMain = new ArrayList<>();
        getConnectedSubstationsViaFeeders(sourceConnectedSubstationMain, connectedSubstationsViaFeedersMain);

        List<String> connectedSubstationsViaFeedersFree = new ArrayList<>();
        getConnectedSubstationsViaFeeders(sourceConnectedSubstationFree, connectedSubstationsViaFeedersFree);

        return !connectedSubstationsViaFeedersMain.contains(sourceConnectedSubstationFree.getUuid())
            && !connectedSubstationsViaFeedersFree.contains(sourceConnectedSubstationMain.getUuid());

    }

    private void getConnectedSubstationsViaFeeders(PNODE substation, List<String> connectedSubstationsViaFeeders) {
        for (BaseConnection connection : substation.getConnections().values()) {
            for (NodeLineDto dto : connection.getNodeLineDtos()) {
                PNODE node = elementService.getNode(dto.getNodeUuid());
                LINE line = elementService.getLine(dto.getLineUuid());
                if (PowerNodeType.LOAD.equals(node.getNodeType()) && !line.isBreaker()) {
                    List<String> ignoreUuids = new ArrayList<>();
                    ignoreUuids.add(substation.getUuid()); // todo скорее всего не надо
                    topologyService.getConnectedFeeders(node, connectedSubstationsViaFeeders, ignoreUuids);
                }


            }
        }
    }

    /**
     * @param mainNode нода типа LOAD, которая принадлежит проверяемому фидеру
     * @return Проверяем, что для данного фидера не достигнуто ограничение по количеству соединений с другими фидерами
     */
    private boolean limitCondition(PNODE mainNode, PNODE freeNode) {
        return limitCondition(mainNode) && limitCondition(freeNode);
    }
    private boolean limitCondition(PNODE mainNode) {
        List<String> connectedFeeders = new ArrayList<>();
        topologyService.getConnectedFeeders(mainNode, connectedFeeders, new ArrayList<>());
        return connectedFeeders.size() <
            baseConfiguration.getLoadConfiguration(mainNode.getVoltageLevels().get(0)).getMaxConnectedFeeders();
    }

    private boolean substationBelongDifferentFeeder(PNODE mainLoad, PNODE freeNode) {
        PNODE sourceConnectedSubstationMain = topologyService.getSourceConnectedSubstation(mainLoad)
            .orElseThrow(() -> new UnsupportedOperationException("Unable to find SourceConnectedSubstation for node : " + mainLoad));
        return !sourceConnectedSubstationMain.getUuid().equals(freeNode.getUuid());
    }

    protected boolean loadsBelongDifferentFeeders(PNODE mainLoad, PNODE freeLoad) {
        // Проверка условия, что две соединяемые нагрузки принадлежат к разным фидерам
        PNODE sourceConnectedSubstationMain = topologyService.getSourceConnectedSubstation(mainLoad)
            .orElseThrow(() -> new UnsupportedOperationException("Unable to find SourceConnectedSubstation for node : " + mainLoad));
        PNODE sourceConnectedSubstationFree = topologyService.getSourceConnectedSubstation(freeLoad)
            .orElseThrow(() -> new UnsupportedOperationException("Unable to find SourceConnectedSubstation for node : " + freeLoad));
        return !sourceConnectedSubstationMain.getUuid().equals(sourceConnectedSubstationFree.getUuid());
    }
}
