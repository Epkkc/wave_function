package com.example.demo.base.service.status;

import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.BasePowerNode;
import com.example.demo.base.service.BaseConfiguration;

public class BaseStatusService extends AbstractStatusService<BasePowerNode> {

    public BaseStatusService(Matrix<BasePowerNode> matrix, BaseConfiguration baseConfiguration, boolean roundedArea) {
        super(matrix, baseConfiguration, roundedArea);
    }
}