package com.example.demo.java.fx.factories;

import com.example.demo.base.factories.PowerNodeFactory;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.power.AbstractPowerNode;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.status.BaseStatus;
import com.example.demo.java.fx.model.power.FxAbstractPowerNode;

public interface FxNodeFactory {

    PowerNodeType getType();
    FxAbstractPowerNode createNode(int x, int y, int power, VoltageLevel... voltageLevels);

}
