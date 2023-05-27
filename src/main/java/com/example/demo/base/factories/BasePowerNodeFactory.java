package com.example.demo.base.factories;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.power.AbstractBasePowerNode;
import com.example.demo.base.model.power.BasePowerNode;

import java.util.Collection;

public class BasePowerNodeFactory implements PowerNodeFactory<BasePowerNode> {

    @Override
    public BasePowerNode createNode(PowerNodeType type, int x, int y, int power, Collection<VoltageLevel> voltageLevels) {
        return new BasePowerNode(type, x, y, power, voltageLevels);
    }

}
