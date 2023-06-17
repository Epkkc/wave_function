package com.example.demo.base.model.status;

import com.example.demo.base.model.enums.VoltageLevel;
import lombok.Getter;

@Getter
public class StatusMetaDto {
    private final VoltageLevel voltageLevel;
    private final int chainLinkOrder;
    private final String nodeUuid;

    public StatusMetaDto(VoltageLevel voltageLevel, int chainLinkOrder, String nodeUuid) {
        this.voltageLevel = voltageLevel;
        this.chainLinkOrder = chainLinkOrder;
        this.nodeUuid = nodeUuid;
    }

}
