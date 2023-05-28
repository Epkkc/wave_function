package com.example.demo.java.fx.factories;

import com.example.demo.java.fx.model.power.FxAbstractPowerNode;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.java.fx.model.power.FxThreeSubStation;
import com.example.demo.java.fx.model.power.FxTwoSubStation;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.java.fx.service.FxElementService;

public class FxSubstationFactory extends FxPowerNodeFactory {

    public FxSubstationFactory(FxElementService elementsService) {
        super(elementsService);
    }

    @Override
    public PowerNodeType getType() {
        return PowerNodeType.SUBSTATION;
    }

    @Override
    public FxAbstractPowerNode createNode(int x, int y, int power, VoltageLevel... voltageLevels) {
        FxAbstractPowerNode node;
        if (voltageLevels.length == 2) {
            node = new FxTwoSubStation(
                x,
                y,
                power,
                voltageLevels[0],
                voltageLevels[1],
                elementsService.getBaseSize()
            );
        } else if (voltageLevels.length == 3) {
            node = new FxThreeSubStation(
                x,
                y,
                power,
                voltageLevels[0],
                voltageLevels[1],
                voltageLevels[2],
                elementsService.getBaseSize()
            );
        } else {
            throw new UnsupportedOperationException("Invalid voltage size = " + voltageLevels.length);
        }

        return node;
    }

}
