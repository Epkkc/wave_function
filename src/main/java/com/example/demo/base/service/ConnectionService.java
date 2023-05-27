package com.example.demo.base.service;

import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.AbstractBasePowerNode;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.power.BaseConnection;

public interface ConnectionService {

    void connectNode(AbstractBasePowerNode<? extends BaseConnection> node, Matrix<? extends AbstractBasePowerNode<? extends BaseConnection>> matrix);
    void connectNodes(AbstractBasePowerNode<? extends BaseConnection> node1, AbstractBasePowerNode<? extends BaseConnection> node2, VoltageLevel voltageLevel);

}
