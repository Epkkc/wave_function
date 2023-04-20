package com.example.demo.base.service;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.BaseLine;
import com.example.demo.base.model.power.BasePowerNode;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class BaseElementService {

    private final Matrix<BasePowerNode> matrix;
    private final List<BaseLine> lines = new ArrayList<>();
    private int sumLoad;
    private int sumPower;

    public void addPowerNodeToGrid(BasePowerNode node) {
        matrix.add(node);
        if (PowerNodeType.LOAD.equals(node.getNodeType())) {
            sumLoad+=node.getPower();
        }
        if (PowerNodeType.GENERATOR.equals(node.getNodeType())) {
            sumPower +=node.getPower();
        }
    }

}
