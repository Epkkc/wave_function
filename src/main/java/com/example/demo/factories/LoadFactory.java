package com.example.demo.factories;

import com.example.demo.model.filter.FilterContext;
import com.example.demo.model.power.node.Generator;
import com.example.demo.model.power.node.Load;
import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.PowerNodeType;
import com.example.demo.model.power.node.VoltageLevel;
import com.example.demo.model.status.PowerNodeStatusMeta;
import com.example.demo.services.ElementServiceImpl;
import com.example.demo.utils.RandomUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class LoadFactory extends PowerNodeFactory{

    public LoadFactory(ElementServiceImpl elementsService) {
        super(elementsService);
    }

    @Override
    public PowerNodeType getType() {
        return PowerNodeType.LOAD;
    }

    @Override
    public PowerNode createNode(int x, int y, int power, VoltageLevel... voltageLevels) {
        Load load = new Load(elementsService.getBaseSize(), power, voltageLevels[0]);
        load.setX(x);
        load.setY(y);
        return load;
    }
}
