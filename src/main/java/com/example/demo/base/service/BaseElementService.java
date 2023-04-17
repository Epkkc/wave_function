package com.example.demo.base.service;

import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.BaseLine;
import com.example.demo.base.model.power.BasePowerNode;
import com.example.demo.java.fx.model.power.FxPowerLine;
import com.example.demo.java.fx.model.power.FxPowerNode;
import javafx.application.Platform;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class BaseElementService {

    private final Matrix<BasePowerNode> matrix;
    private final List<BaseLine> lines = new ArrayList<>();

    public void addPowerNodeToGrid(BasePowerNode node) {
        matrix.add(node);
    }

}
