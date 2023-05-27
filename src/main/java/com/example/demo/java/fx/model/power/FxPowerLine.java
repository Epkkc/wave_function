package com.example.demo.java.fx.model.power;

import com.example.demo.base.model.enums.VoltageLevel;
import javafx.scene.shape.Shape;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FxPowerLine {
    protected FxAbstractPowerNode point1;
    protected FxAbstractPowerNode point2;
    protected VoltageLevel voltageLevel;
    protected String uuid = UUID.randomUUID().toString();
    protected Shape line;

    public FxPowerLine(FxAbstractPowerNode point1, FxAbstractPowerNode point2, VoltageLevel voltageLevel, Shape line) {
        this.point1 = point1;
        this.point2 = point2;
        this.voltageLevel = voltageLevel;
        this.line = line;
    }
}
