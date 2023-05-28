package com.example.demo.java.fx.factories;

import com.example.demo.base.factories.PowerNodeFactory;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.java.fx.model.power.FxAbstractPowerNode;
import com.example.demo.java.fx.service.FxElementService;

import java.util.Collection;
import java.util.HashMap;

public class FxPowerNodeAbstractFactory implements PowerNodeFactory<FxAbstractPowerNode> {

    private final HashMap<PowerNodeType, FxNodeFactory> factoriesMap = new HashMap<>();

    public FxPowerNodeAbstractFactory(FxElementService elementsService) {
        // TODO:SPRING От этого конструктора можно будет отказаться, потому что elementService будет автоваириться в SubstationFactory
        //  также, поскольку HashMap будет заполняться автоматически
        factoriesMap.put(PowerNodeType.SUBSTATION, new FxSubstationFactory(elementsService));
        factoriesMap.put(PowerNodeType.GENERATOR, new FxGeneratorFactory(elementsService));
        factoriesMap.put(PowerNodeType.LOAD, new FxLoadFactory(elementsService));
    }

    @Override
    public FxAbstractPowerNode createNode(PowerNodeType type, int x, int y, int power, Collection<VoltageLevel> voltageLevels) {
        return factoriesMap.get(type).createNode(x, y, power, voltageLevels.toArray(VoltageLevel[]::new));
    }

}
