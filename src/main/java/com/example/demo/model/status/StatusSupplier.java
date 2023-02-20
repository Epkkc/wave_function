package com.example.demo.model.status;

import com.example.demo.model.power.node.PowerNode;

import java.util.List;
import java.util.Set;

// TODO Убрать все вызовы в StatusService
public interface StatusSupplier {

    Set<PowerNodeStatusMeta> getAllPowerNodeStatuses();
    List<StatusMeta> getStatusByNode(PowerNode node);

}
