package com.example.demo.java.fx.model.power;

import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.power.AbstractLine;
import javafx.scene.shape.Shape;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FxPowerLine extends AbstractLine<FxAbstractPowerNode> {
    protected Shape line;

    public FxPowerLine(FxAbstractPowerNode point1, FxAbstractPowerNode point2, VoltageLevel voltageLevel, boolean breaker, Shape line) {
        super(point1, point2, voltageLevel, breaker);
        this.line = line;
    }
}
