package com.example.demo.java.fx.factories;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.power.LevelChainNumberDto;
import com.example.demo.java.fx.model.power.FxAbstractPowerNode;
import com.example.demo.java.fx.model.power.FxLoad;
import com.example.demo.java.fx.service.FxElementService;

import java.util.List;

public class FxLoadFactory extends FxPowerNodeFactory {

    public FxLoadFactory(FxElementService elementsService) {
        super(elementsService);
    }

    @Override
    public PowerNodeType getType() {
        return PowerNodeType.LOAD;
    }

    @Override
    public FxAbstractPowerNode createNode(int x, int y, int power, List<LevelChainNumberDto> levelChainNumberDtos) {
        assert levelChainNumberDtos.size() == 1;
        return new FxLoad(x, y, power, levelChainNumberDtos, elementsService.getBaseSize());
    }
}
