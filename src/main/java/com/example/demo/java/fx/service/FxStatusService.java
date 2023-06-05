package com.example.demo.java.fx.service;

import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.service.BaseConfiguration;
import com.example.demo.base.service.status.AbstractStatusService;
import com.example.demo.java.fx.model.power.FxAbstractPowerNode;

public class FxStatusService extends AbstractStatusService<FxAbstractPowerNode> {

    public FxStatusService(Matrix<FxAbstractPowerNode> matrix, BaseConfiguration baseConfiguration) {
        super(matrix, baseConfiguration);
    }

}