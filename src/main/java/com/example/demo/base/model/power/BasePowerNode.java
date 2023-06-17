package com.example.demo.base.model.power;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.status.BaseStatus;
import com.example.demo.base.model.status.StatusMetaDto;
import com.example.demo.base.model.status.StatusType;

import java.util.Collection;
import java.util.List;

public class BasePowerNode extends AbstractPowerNode<BaseStatus, BaseConnection> {

    public BasePowerNode(PowerNodeType nodeType, int x, int y, int power, List<LevelChainNumberDto> levelChainNumberDtos) {
        super(nodeType, x, y, power, levelChainNumberDtos);
    }

    @Override
    protected void initConnections(List<LevelChainNumberDto> levelChainNumberDtos) {
        for (LevelChainNumberDto dto : levelChainNumberDtos) {
            connections.put(dto.getVoltageLevel(), new BaseConnection(dto.getVoltageLevel(), dto.getChainLinkNumber()));
        }
    }

    @Override
    protected BaseStatus getStatus(StatusType statusType, Collection<StatusMetaDto> statusDtos) {
        return new BaseStatus(statusType, statusDtos);
    }


}
