package com.example.demo.base.model.status;

import com.example.demo.base.model.enums.VoltageLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class StatusLevelChainLinkDto {
    private final VoltageLevel voltageLevel;
    private final int chainLinkOrder;
    private final String nodeUuid;
}
