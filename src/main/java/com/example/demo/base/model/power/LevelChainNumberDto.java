package com.example.demo.base.model.power;

import com.example.demo.base.model.enums.VoltageLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LevelChainNumberDto {
    private VoltageLevel voltageLevel;
    private int chainLinkNumber;
}
