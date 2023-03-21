package com.example.demo.factories;

import com.example.demo.model.filter.FilterContext;
import com.example.demo.model.power.node.Generator;
import com.example.demo.model.power.node.Load;
import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.PowerNodeType;
import com.example.demo.model.status.PowerNodeStatusMeta;
import com.example.demo.services.ElementServiceImpl;
import com.example.demo.utils.RandomUtils;

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
    public Optional<PowerNode> createNode(FilterContext context) {
        PowerNodeStatusMeta status = findStatus(context);
        Load load = new Load(elementsService.getBaseSize(), RandomUtils.randomValue(status.voltageLevels()));
        load.setX(context.node().getX());
        load.setY(context.node().getY());
        return Optional.of(load);
    }
}
