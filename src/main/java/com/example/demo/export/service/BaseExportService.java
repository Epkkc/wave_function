package com.example.demo.export.service;

import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.BaseLine;
import com.example.demo.base.model.power.BasePowerNode;
import com.example.demo.base.service.BaseConfiguration;
import com.example.demo.base.service.element.BaseElementService;

public class BaseExportService extends AbstractExportService<BasePowerNode, BaseLine> {

    public BaseExportService(BaseConfiguration configuration, BaseElementService elementService, Matrix<BasePowerNode> matrix) {
        super(configuration, elementService, matrix);
    }
}
