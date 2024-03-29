package com.example.demo.java.fx.model.power;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import javafx.beans.property.DoubleProperty;
import javafx.scene.paint.Color;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FxBaseNode extends FxAbstractPowerNode {

    public FxBaseNode(int x, int y, double size) {
        super(PowerNodeType.EMPTY, x, y, 0, List.of(), size);
    }

    @Override
    protected void setOpacity(VoltageLevel voltageLevel, double value) {
    }

    @Override
    public void setStrokeColor(Color color) {

    }

    @Override
    public Collection<DoubleProperty> getOpacityProperty() {
        return Collections.emptyList();
    }
}
