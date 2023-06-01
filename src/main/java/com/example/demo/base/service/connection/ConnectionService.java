package com.example.demo.base.service.connection;

import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.power.AbstractPowerNode;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.status.BaseStatus;

import java.util.Set;

public interface ConnectionService<PNODE extends AbstractPowerNode<? extends BaseStatus, ? extends BaseConnection>> {

    void connectNode(PNODE node);

    void connectNode(PNODE node, Set<String> ignoreUuids);

    void connectNodes(PNODE node1, PNODE node2, VoltageLevel voltageLevel);

    void connectNodes(PNODE node1, PNODE node2, VoltageLevel voltageLevel, boolean breaker);

}
