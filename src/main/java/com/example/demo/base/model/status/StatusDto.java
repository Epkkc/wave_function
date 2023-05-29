package com.example.demo.base.model.status;

import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.status.StatusType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
@Getter
@Deprecated(forRemoval = true) //todo удалить, если не пригодится
public class StatusDto {

    private final StatusType statusType;
    private final Set<VoltageLevel> voltageLevel;

}
