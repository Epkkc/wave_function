package com.example.demo.base.model.status;

import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.status.StatusType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class StatusDto {

    private final StatusType statusType;
    private final VoltageLevel voltageLevel;

}
