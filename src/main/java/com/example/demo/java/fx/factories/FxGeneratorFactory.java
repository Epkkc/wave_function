package com.example.demo.java.fx.factories;

import com.example.demo.base.model.power.LevelChainNumberDto;
import com.example.demo.java.fx.model.power.FxGenerator;
import com.example.demo.java.fx.model.power.FxAbstractPowerNode;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.java.fx.service.FxElementService;

import java.util.List;

public class FxGeneratorFactory extends FxPowerNodeFactory {

    public FxGeneratorFactory(FxElementService elementsService) {
        super(elementsService);
    }

    @Override
    public PowerNodeType getType() {
        return PowerNodeType.GENERATOR;
    }

    @Override
    public FxAbstractPowerNode createNode(int x, int y, int power, List<LevelChainNumberDto> levelChainNumberDtos) {
        assert levelChainNumberDtos.size() == 1;
        return new FxGenerator(x, y, power, levelChainNumberDtos, elementsService.getBaseSize());
    }

}
