package com.example.demo.model.filter;

import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.status.PowerNodeStatusMeta;
import com.example.demo.model.status.Status;
import com.example.demo.model.status.StatusMeta;

import java.util.Set;

public record FilterContext(
    Set<PowerNodeStatusMeta> possibleStatuses,
    Set<Status> mustStatuses,
    PowerNode node) {}
