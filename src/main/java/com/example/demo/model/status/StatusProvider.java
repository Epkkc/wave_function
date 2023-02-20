package com.example.demo.model.status;

import com.example.demo.factories.PowerNodeFactory;
import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.PowerNodeType;

import java.util.List;

public interface StatusProvider {
    List<StatusMeta> provideStatuses(PowerNode node);

    PowerNodeType getType();
}
