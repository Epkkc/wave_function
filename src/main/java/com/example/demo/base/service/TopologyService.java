package com.example.demo.base.service;

import com.example.demo.base.model.power.AbstractLine;
import com.example.demo.base.model.power.AbstractPowerNode;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.status.BaseStatus;

import java.util.List;
import java.util.Optional;

public interface TopologyService<PNODE extends AbstractPowerNode<? extends BaseStatus, ? extends BaseConnection>, LINE extends AbstractLine<PNODE>> {

    Optional<PNODE> getSourceConnectedSubstation(PNODE load);

    Optional<PNODE> getConnectedSubstation(PNODE load);

    PNODE getSourceSubstation(String nodeUuid);

    void getConnectedFeeders(PNODE load, List<String> connectedFeedersUuid, List<String> ignoredUuids);
}
