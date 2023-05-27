package com.example.demo.base.service.status;

import com.example.demo.base.model.configuration.GenerationConfiguration;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.TransformerConfiguration;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.AbstractBasePowerNode;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.power.BasePowerNode;
import com.example.demo.base.model.status.StatusType;
import com.example.demo.base.service.BaseConfiguration;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class BaseStatusService extends AbstractStatusService<BasePowerNode> {


    public BaseStatusService(Matrix<BasePowerNode> matrix, BaseConfiguration baseConfiguration, boolean roundedArea) {
        super(matrix, baseConfiguration, roundedArea);
    }
}