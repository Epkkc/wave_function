package com.example.demo.base.model.power;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.status.BaseStatus;
import com.example.demo.base.model.status.StatusLevelChainLinkDto;
import com.example.demo.base.model.status.StatusType;

import java.util.Collection;

public class BasePowerNode extends AbstractPowerNode<BaseStatus, BaseConnection> {

    public BasePowerNode(PowerNodeType nodeType, int x, int y, int power, int chainLinkOrder, Collection<VoltageLevel> voltageLevels) {
        super(nodeType, x, y, chainLinkOrder, power, voltageLevels);
    }

    @Override
    protected void initConnections() {
        voltageLevels.forEach(level -> connections.put(level, new BaseConnection(level, 0, 2)));
    }

    @Override
    protected BaseStatus getStatus(StatusType statusType, Collection<StatusLevelChainLinkDto> statusDtos) {
        return new BaseStatus(statusType, statusDtos);
    }


}
