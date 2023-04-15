package com.example.demo.services;

import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.VoltageLevel;
import lombok.RequiredArgsConstructor;

public interface ConnectionService {

    void connectNode(PowerNode node);
    void connectNodes(PowerNode node1, PowerNode node2, VoltageLevel voltageLevel);

}
