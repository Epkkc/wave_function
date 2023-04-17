package com.example.demo.java.fx.model.power;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;

import java.util.List;

public class FxBaseNode extends FxPowerNode {

    public FxBaseNode(int x, int y, double size) {
        super(PowerNodeType.EMPTY, x, y, 0, List.of(), size);
    }

    @Override
    protected void setOpacity(VoltageLevel voltageLevel, double value) {}
}
