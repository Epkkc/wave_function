package com.example.demo.base.service.connection;

import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.AbstractBasePowerNode;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.power.BaseConnection;

public interface ConnectionService<T extends AbstractBasePowerNode<? extends BaseConnection>> {

    void connectNode(T node, Matrix<T> matrix);
    void connectNodes(T node1, T node2, VoltageLevel voltageLevel);

}
