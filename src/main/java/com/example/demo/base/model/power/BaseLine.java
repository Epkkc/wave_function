package com.example.demo.base.model.power;

import com.example.demo.base.model.enums.VoltageLevel;
import lombok.Getter;

@Getter
public class BaseLine extends AbstractLine<AbstractBasePowerNode> {

    public BaseLine(AbstractBasePowerNode point1, AbstractBasePowerNode point2, VoltageLevel voltageLevel) {
        super(point1, point2, voltageLevel, false); // todo переделать, когда появится логика определения breaker
    }

}
