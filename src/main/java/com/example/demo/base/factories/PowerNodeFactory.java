package com.example.demo.base.factories;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.power.AbstractBasePowerNode;
import com.example.demo.base.model.power.BaseConnection;

import java.util.Collection;

public interface PowerNodeFactory<T extends AbstractBasePowerNode<? extends BaseConnection>> {

    T createNode(PowerNodeType type, int x, int y, int power, Collection<VoltageLevel> voltageLevels);

}
