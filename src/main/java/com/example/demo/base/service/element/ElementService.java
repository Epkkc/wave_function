package com.example.demo.base.service.element;

import com.example.demo.base.model.power.AbstractBasePowerNode;
import com.example.demo.base.model.power.AbstractLine;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.status.BaseStatus;

import java.util.List;

public interface ElementService<PNODE extends AbstractBasePowerNode<? extends BaseStatus, ? extends BaseConnection>, LINE extends AbstractLine<PNODE>> {
    void addPowerNodeToGrid(PNODE node);
    void addEdge(int value);
    void addNode(int value);
    List<LINE> getLines();
}
