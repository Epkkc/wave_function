package com.example.demo.base.model.power;

import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.status.BaseStatus;
import lombok.Data;

import java.util.UUID;

@Data
public abstract class AbstractLine<PNODE extends AbstractPowerNode<? extends BaseStatus, ? extends BaseConnection>> {

    protected PNODE point1;
    protected PNODE point2;
    protected VoltageLevel voltageLevel;
    protected String uuid = UUID.randomUUID().toString();
    protected boolean breaker;

    public AbstractLine(PNODE point1, PNODE point2, VoltageLevel voltageLevel, boolean breaker) {
        this.point1 = point1;
        this.point2 = point2;
        this.voltageLevel = voltageLevel;
        this.breaker = breaker;
    }
}
