package com.example.demo.base.service.element;

import com.example.demo.base.model.power.AbstractBasePowerNode;
import com.example.demo.base.model.power.BaseConnection;

public interface ElementService<T extends AbstractBasePowerNode<? extends BaseConnection>> {
    void addPowerNodeToGrid(T node);
    void addEdge(int value);
    void addNode(int value);
}
