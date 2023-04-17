package com.example.demo.java.fx.factories;

import com.example.demo.java.fx.model.power.FxLoad;
import com.example.demo.java.fx.model.power.FxPowerNode;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.services.FxElementService;

public class FxLoadFactory extends FxPowerNodeFactory {

    public FxLoadFactory(FxElementService elementsService) {
        super(elementsService);
    }

    @Override
    public PowerNodeType getType() {
        return PowerNodeType.LOAD;
    }

    @Override
    public FxPowerNode createNode(int x, int y, int power, VoltageLevel... voltageLevels) {
        assert voltageLevels.length == 1;
        return new FxLoad(x, y, power, voltageLevels[0], elementsService.getBaseSize());
    }
}
