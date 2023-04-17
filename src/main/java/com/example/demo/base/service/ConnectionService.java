package com.example.demo.base.service;

import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.BasePowerNode;
import com.example.demo.java.fx.model.power.FxPowerNode;
import com.example.demo.base.model.enums.VoltageLevel;

public interface ConnectionService {

    void connectNode(BasePowerNode node, Matrix<? extends BasePowerNode> matrix);
    void connectNodes(BasePowerNode node1, BasePowerNode node2, VoltageLevel voltageLevel);

}
