package com.example.demo.java.fx.factories;

import com.example.demo.base.factories.PowerNodeFactory;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.power.LevelChainNumberDto;
import com.example.demo.java.fx.model.power.FxAbstractPowerNode;
import com.example.demo.java.fx.service.FxElementService;

import java.util.HashMap;
import java.util.List;

public class FxPowerNodeAbstractFactory implements PowerNodeFactory<FxAbstractPowerNode> {

    private final HashMap<PowerNodeType, FxNodeFactory> factoriesMap = new HashMap<>();

    public FxPowerNodeAbstractFactory(FxElementService elementsService) {
        factoriesMap.put(PowerNodeType.SUBSTATION, new FxSubstationFactory(elementsService));
        factoriesMap.put(PowerNodeType.GENERATOR, new FxGeneratorFactory(elementsService));
        factoriesMap.put(PowerNodeType.LOAD, new FxLoadFactory(elementsService));
    }

    @Override
    public FxAbstractPowerNode createNode(PowerNodeType type, int x, int y, int power, List<LevelChainNumberDto> levelChainNumberDtos) {
        return factoriesMap.get(type).createNode(x, y, power, levelChainNumberDtos);
    }

}
