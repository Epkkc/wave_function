package com.example.demo.base.factories;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.power.BasePowerNode;
import com.example.demo.base.model.power.LevelChainNumberDto;

import java.util.Collection;
import java.util.List;

public class BasePowerNodeFactory implements PowerNodeFactory<BasePowerNode> {

    @Override
    public BasePowerNode createNode(PowerNodeType type, int x, int y, int power, List<LevelChainNumberDto> levelChainNumberDtos) {
        return new BasePowerNode(type, x, y, power, levelChainNumberDtos);
    }

}
