package com.example.demo.base.service;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.power.AbstractLine;
import com.example.demo.base.model.power.AbstractPowerNode;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.power.NodeLineDto;
import com.example.demo.base.model.status.BaseStatus;
import com.example.demo.base.service.element.ElementService;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseTopologyService<PNODE extends AbstractPowerNode<? extends BaseStatus, ? extends BaseConnection>, LINE extends AbstractLine<PNODE>> implements TopologyService<PNODE, LINE> {

    protected final ElementService<PNODE, LINE> elementService;

    /**
     * @param load нода типа LOAD
     * @return питающую SUBSTATION фидера, к которому принадлежит load
     */
    public Optional<PNODE> getSourceConnectedSubstation(PNODE load) {
        if (!PowerNodeType.LOAD.equals(load.getNodeType())) {
            throwInvalidLoadException(load);
        }

        int loadChainLinkOrder = getLoadChainLinkOrder(load);
        if (loadChainLinkOrder > 1) {
            // Если chainLinkNumber > 1, то нужно вызвать этот же метод для соединённой нагрузки с chainLinkNumber - 1
            for (BaseConnection connection : load.getConnections().values()) {
                for (NodeLineDto dto : connection.getNodeLineDtos()) {
                    PNODE node = elementService.getNode(dto.getNodeUuid());
                    LINE line = elementService.getLine(dto.getLineUuid());
                    if (node != null && PowerNodeType.LOAD.equals(node.getNodeType()) && getLoadChainLinkOrder(node) == (loadChainLinkOrder - 1) && !line.isBreaker()) {
                        return getSourceConnectedSubstation(node);
                    }
                }
            }
        } else {
            // Иначе (chainLinkNumber == 1) вернуть результат метода getConnectedSubstation(load)
            return getConnectedSubstation(load);
        }

        return Optional.empty();
    }

    private int getLoadChainLinkOrder(PNODE load) {
        if (!PowerNodeType.LOAD.equals(load.getNodeType())) {
            throwInvalidLoadException(load);
        }

        return load.getConnections().get(load.getVoltageLevels().get(0)).getChainLinkOrder();
    }

    /**
     * @param load нода типа LOAD
     * @return подстанцию, с которой соединена нода (реализация подразумевает, что LOAD может быть связан только с одной SUBSTATION)
     */
    public Optional<PNODE> getConnectedSubstation(PNODE load) {
        for (BaseConnection value : load.getConnections().values()) {
            for (NodeLineDto dto : value.getNodeLineDtos()) {
                PNODE node = elementService.getNode(dto.getNodeUuid());
                LINE line = elementService.getLine(dto.getLineUuid());
                // Если нагрузка соединена с SUBSTATION посредством breaker-а, то эта SUBSTATION не является питающей для фидера, к которому принадлежит load
                if (node != null && PowerNodeType.SUBSTATION.equals(node.getNodeType()) && !line.isBreaker()) {
                    return Optional.of(node);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * @param parentUuid uuid предка, предком может быть как SUBSTATION, так и LOAD
     * @return питающую SUBSTATION для фидера, к которому принадлежит parentUuid
     */
    @Override
    public PNODE getSourceSubstation(String parentUuid) {
        PNODE parentNode = elementService.getNode(parentUuid);

        if (parentNode != null && !PowerNodeType.LOAD.equals(parentNode.getNodeType()) && !PowerNodeType.SUBSTATION.equals(parentNode.getNodeType())) {
            throw new UnsupportedOperationException("Wrong parameter \"parentUuid\" because parentNode is not SUBSTATION or LOAD: " + parentNode);
        }

        if (PowerNodeType.SUBSTATION.equals(parentNode.getNodeType())) {
            return parentNode;
        } else {
            return getSourceConnectedSubstation(parentNode).orElseThrow(() -> new UnsupportedOperationException("Unable to find source substation of load :" + parentNode));
        }
    }

    public void getConnectedFeeders(PNODE load, List<String> connectedFeedersUuid, List<String> ignoredUuids) {
        if (!PowerNodeType.LOAD.equals(load.getNodeType())) {
            throwInvalidLoadException(load);
        }

        if (ignoredUuids.contains(load.getUuid())){
            return;
        }
        ignoredUuids.add(load.getUuid());

        for (BaseConnection connection : load.getConnections().values()) {
            for (NodeLineDto dto : connection.getNodeLineDtos()) {
                PNODE connectedNode = elementService.getNode(dto.getNodeUuid());
                LINE line = elementService.getLine(dto.getLineUuid());
                if (PowerNodeType.SUBSTATION.equals(connectedNode.getNodeType()) && line.isBreaker()) {
                    // todo неактуально сейчас, поскольку нет возможности соединять нагрузки напрямую с ПС через breaker
                    connectedFeedersUuid.add(connectedNode.getUuid());
                } else if (PowerNodeType.LOAD.equals(connectedNode.getNodeType()) && line.isBreaker()) {
                    PNODE sourceSubstation = getSourceSubstation(connectedNode.getUuid());
                    connectedFeedersUuid.add(sourceSubstation.getUuid());
                } else if (PowerNodeType.LOAD.equals(connectedNode.getNodeType())) {
                    getConnectedFeeders(connectedNode, connectedFeedersUuid, ignoredUuids);
                }
            }
        }
    }

    private void throwInvalidLoadException(PNODE load) {
        throw new UnsupportedOperationException("Wrong parameter \"load\" : " + load);
    }
}
