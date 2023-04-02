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
    public Optional<PowerNode> createNode(FilterContext context) {
        PowerNodeStatusMeta status = findStatus(context);
        Generator generator = new Generator(elementsService.getBaseSize(), RandomUtils.randomValue(status.voltageLevels()));
        generator.setX(context.node().getX());
        generator.setY(context.node().getY());
        return Optional.of(generator);
    }

}
