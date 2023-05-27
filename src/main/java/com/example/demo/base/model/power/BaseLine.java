package com.example.demo.base.model.power;

import com.example.demo.base.model.enums.VoltageLevel;
import lombok.Getter;

@Getter
public class BaseLine extends AbstractLine<BasePowerNode> {
    public BaseLine(BasePowerNode point1, BasePowerNode point2, VoltageLevel voltageLevel, boolean breaker) {
        super(point1, point2, voltageLevel, breaker);
    }
}
