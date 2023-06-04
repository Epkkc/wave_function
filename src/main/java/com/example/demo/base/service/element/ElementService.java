package com.example.demo.base.service.element;

import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.AbstractLine;
import com.example.demo.base.model.power.AbstractPowerNode;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.status.BaseStatus;

import java.util.List;
import java.util.Optional;

public interface ElementService<PNODE extends AbstractPowerNode<? extends BaseStatus, ? extends BaseConnection>, LINE extends AbstractLine<PNODE>> {
    void addPowerNodeToGrid(PNODE node);

    void addLine(LINE line);

    void removeLine(LINE line, boolean fromRemoveNodeMethod);

    void removeLine(LINE line);

    void removeNode(PNODE node, PNODE replaceNode);

    List<LINE> getLines();

    LINE getLine(String uuid);

    Matrix<PNODE> getMatrix();

    int getTotalNumberOfNodes();

    int getTotalNumberOfEdges();

    int getSumLoad();

    int getSumPower();

    PNODE getNode(String uuid);

    List<PNODE> getNodes();

    List<PNODE> getAllGenerators();
}
