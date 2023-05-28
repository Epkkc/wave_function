package com.example.demo.base.factories;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.power.AbstractPowerNode;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.status.BaseStatus;

import java.util.Collection;

public class AbstractPowerNodeFactory<PNODE extends AbstractPowerNode<? extends BaseStatus, ? extends BaseConnection>> implements PowerNodeFactory<PNODE> {


    @Override
    public PNODE createNode(PowerNodeType type, int x, int y, int power, Collection<VoltageLevel> voltageLevels) {
        return null;
    }

}
