package com.example.demo.java.fx.factories;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.power.LevelChainNumberDto;
import com.example.demo.java.fx.model.power.FxAbstractPowerNode;

import java.util.List;

public interface FxNodeFactory {

    PowerNodeType getType();

    FxAbstractPowerNode createNode(int x, int y, int power, List<LevelChainNumberDto> levelChainNumberDtos);

}
