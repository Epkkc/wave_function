package com.example.demo.procedure;

import com.example.demo.factories.GeneratorFactory;
import com.example.demo.factories.LoadFactory;
import com.example.demo.factories.PowerNodeFactory;
import com.example.demo.factories.SubstationFactory;
import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.PowerNodeType;
import com.example.demo.model.power.node.ThreeWSubStation;
import com.example.demo.model.power.node.TwoWSubStation;
import com.example.demo.model.power.node.VoltageLevel;
import com.example.demo.services.ElementServiceImpl;

import java.util.HashMap;
import java.util.Random;

public class AbstractNodeFabric {

    private final ElementServiceImpl elementsService;
    private final HashMap<PowerNodeType, PowerNodeFactory> factoriesMap = new HashMap<>();
    private final Random random = new Random();


    public AbstractNodeFabric(ElementServiceImpl elementsService) {
        this.elementsService = elementsService;
        // TODO:SPRING От этого конструктора можно будет отказаться, потому что elementService будет автоваириться в SubstationFactory
        //  также, поскольку HashMap будет заполняться автоматически
        factoriesMap.put(PowerNodeType.SUBSTATION, new SubstationFactory(elementsService));
        factoriesMap.put(PowerNodeType.GENERATOR, new GeneratorFactory(elementsService)); // TODO раскоментить
        factoriesMap.put(PowerNodeType.LOAD, new LoadFactory(elementsService));
    }


    public PowerNode createTwoWindingsSubstation(VoltageLevel voltageLevel1, VoltageLevel voltageLevel2, PowerNode node) {
        TwoWSubStation twoWSubStation = new TwoWSubStation(
            elementsService.getBaseSize(),
            voltageLevel1,
            voltageLevel2
        );
        twoWSubStation.setX(node.getX());
        twoWSubStation.setY(node.getY());

        return twoWSubStation;
    }

    public PowerNode createThreeWindingsSubstation(VoltageLevel voltageLevel1, VoltageLevel voltageLevel2, VoltageLevel voltageLevel3, PowerNode node) {
        ThreeWSubStation threeWSubStation = new ThreeWSubStation(
            elementsService.getBaseSize(),
            voltageLevel1,
            voltageLevel2,
            voltageLevel3
        );
        threeWSubStation.setX(node.getX());
        threeWSubStation.setY(node.getY());

        return threeWSubStation;
    }
}
