package com.example.demo.services;

import com.example.demo.model.power.node.PowerNode;
import lombok.RequiredArgsConstructor;

public interface ConnectionService {

    void connectNode(PowerNode node);

}
