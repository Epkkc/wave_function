package com.example.demo.base.factories;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.power.AbstractPowerNode;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.power.LevelChainNumberDto;
import com.example.demo.base.model.status.BaseStatus;

import java.util.List;

public interface PowerNodeFactory<PNODE extends AbstractPowerNode<? extends BaseStatus, ? extends BaseConnection>> {

    PNODE createNode(PowerNodeType type, int x, int y, int power, List<LevelChainNumberDto> levelChainNumberDtos);

}
