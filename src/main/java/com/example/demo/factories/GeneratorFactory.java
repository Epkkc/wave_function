package com.example.demo.factories;

import com.example.demo.model.filter.FilterContext;
import com.example.demo.model.power.node.Generator;
import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.PowerNodeType;
import com.example.demo.model.power.node.VoltageLevel;
import com.example.demo.model.status.PowerNodeStatusMeta;
import com.example.demo.services.ElementServiceImpl;
import com.example.demo.utils.RandomUtils;

import java.util.Optional;

public class GeneratorFactory extends PowerNodeFactory{

    public GeneratorFactory(ElementServiceImpl elementsService) {
        super(elementsService);
    }

    @Override
    public PowerNodeType getType() {
        return PowerNodeType.GENERATOR;
    }

    @Override
    public PowerNode createNode(int x, int y, int power, VoltageLevel... voltageLevels) {
        Generator generator = new Generator(elementsService.getBaseSize(), power, voltageLevels[0]);
        generator.setX(x);
        generator.setY(y);
        return generator;
    }

}
