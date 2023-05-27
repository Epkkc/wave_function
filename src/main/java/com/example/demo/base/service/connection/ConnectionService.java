package com.example.demo.base.service.connection;

import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.AbstractPowerNode;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.status.BaseStatus;

public interface ConnectionService<PNODE extends AbstractPowerNode<? extends BaseStatus, ? extends BaseConnection>> {

    void connectNode(PNODE node, Matrix<PNODE> matrix);
    void connectNodes(PNODE node1, PNODE node2, VoltageLevel voltageLevel);

}
