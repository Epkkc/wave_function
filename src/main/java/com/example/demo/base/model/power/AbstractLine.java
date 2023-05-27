package com.example.demo.base.model.power;

import com.example.demo.base.model.enums.VoltageLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
public abstract class AbstractLine<T extends BasePowerNode> {

    protected T point1;
    protected T point2;
    protected VoltageLevel voltageLevel;
    protected String uuid = UUID.randomUUID().toString();
    protected boolean breaker;

    public AbstractLine(T point1, T point2, VoltageLevel voltageLevel, boolean breaker) {
        this.point1 = point1;
        this.point2 = point2;
        this.voltageLevel = voltageLevel;
        this.breaker = breaker;
    }
}
