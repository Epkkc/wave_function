package com.example.demo.base.model.power;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;

import java.util.Collection;

public class BasePowerNode extends AbstractBasePowerNode<BaseConnection> {

    public BasePowerNode(PowerNodeType nodeType, int x, int y, int power, Collection<VoltageLevel> voltageLevels) {
        super(nodeType, x, y, power, voltageLevels);
    }

    @Override
    void initConnections() {
        voltageLevels.forEach(level -> connections.put(level, new BaseConnection(level, 0, 2)));
    }

}
