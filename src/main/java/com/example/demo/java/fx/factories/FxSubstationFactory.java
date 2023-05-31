package com.example.demo.java.fx.factories;

import com.example.demo.base.model.power.LevelChainNumberDto;
import com.example.demo.java.fx.model.power.FxAbstractPowerNode;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.java.fx.model.power.FxThreeSubStation;
import com.example.demo.java.fx.model.power.FxTwoSubStation;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.java.fx.service.FxElementService;

import java.util.List;

public class FxSubstationFactory extends FxPowerNodeFactory {

    public FxSubstationFactory(FxElementService elementsService) {
        super(elementsService);
    }

    @Override
    public PowerNodeType getType() {
        return PowerNodeType.SUBSTATION;
    }

    @Override
    public FxAbstractPowerNode createNode(int x, int y, int power, List<LevelChainNumberDto> levelChainNumberDtos) {
        FxAbstractPowerNode node;
        if (levelChainNumberDtos.size() == 2) {
            node = new FxTwoSubStation(
                x,
                y,
                power,
                levelChainNumberDtos,
                elementsService.getBaseSize()
            );
        } else if (levelChainNumberDtos.size() == 3) {
            node = new FxThreeSubStation(
                x,
                y,
                power,
                levelChainNumberDtos,
                elementsService.getBaseSize()
            );
        } else {
            throw new UnsupportedOperationException("Invalid voltage size = " + levelChainNumberDtos.size());
        }

        return node;
    }

}
