package com.example.demo.procedure;

import com.example.demo.factories.GeneratorFactory;
import com.example.demo.factories.LoadFactory;
import com.example.demo.factories.PowerNodeFactory;
import com.example.demo.factories.SubstationFactory;
import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.PowerNodeType;
import com.example.demo.model.power.node.VoltageLevel;
import com.example.demo.services.ElementServiceImpl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public class AbstractNodeFabric {

    private final HashMap<PowerNodeType, PowerNodeFactory> factoriesMap = new HashMap<>();

    public AbstractNodeFabric(ElementServiceImpl elementsService) {
        // TODO:SPRING От этого конструктора можно будет отказаться, потому что elementService будет автоваириться в SubstationFactory
        //  также, поскольку HashMap будет заполняться автоматически
        factoriesMap.put(PowerNodeType.SUBSTATION, new SubstationFactory(elementsService));
        factoriesMap.put(PowerNodeType.GENERATOR, new GeneratorFactory(elementsService));
        factoriesMap.put(PowerNodeType.LOAD, new LoadFactory(elementsService));
    }

    public PowerNode createNode(PowerNodeType type, int x, int y, int power, VoltageLevel... voltageLevels) {
        return factoriesMap.get(type).createNode(x, y, power, voltageLevels);
    }

    public PowerNode createNode(PowerNodeType type, int x, int y, int power, Collection<VoltageLevel> voltageLevels) {
        return factoriesMap.get(type).createNode(x, y, power, voltageLevels.toArray(VoltageLevel[]::new));
    }

}
