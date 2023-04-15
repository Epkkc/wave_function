package com.example.demo.factories;

import com.example.demo.model.filter.FilterContext;
import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.PowerNodeType;
import com.example.demo.model.power.node.VoltageLevel;
import com.example.demo.model.status.PowerNodeStatusMeta;
import com.example.demo.services.ElementServiceImpl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Getter
public abstract class PowerNodeFactory {

    protected final ElementServiceImpl elementsService;

    public abstract PowerNodeType getType();

    public abstract PowerNode createNode(int x, int y, int power, VoltageLevel... voltageLevels);


}
