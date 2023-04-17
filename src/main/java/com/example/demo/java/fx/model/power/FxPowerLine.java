package com.example.demo.java.fx.model.power;

import com.example.demo.base.model.enums.VoltageLevel;
import javafx.scene.shape.Line;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FxPowerLine {
    protected FxPowerNode point1;
    protected FxPowerNode point2;
    protected VoltageLevel voltageLevel;
    protected String uuid = UUID.randomUUID().toString();
    protected Line line;

    public FxPowerLine(FxPowerNode point1, FxPowerNode point2, VoltageLevel voltageLevel, Line line) {
        this.point1 = point1;
        this.point2 = point2;
        this.voltageLevel = voltageLevel;
        this.line = line;
    }
}
