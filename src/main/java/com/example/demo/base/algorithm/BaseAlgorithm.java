package com.example.demo.base.algorithm;

import com.example.demo.base.factories.PowerNodeFactory;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.BaseLine;
import com.example.demo.base.model.power.BasePowerNode;
import com.example.demo.base.service.BaseConfiguration;
import com.example.demo.base.service.TopologyService;
import com.example.demo.base.service.connection.ConnectionService;
import com.example.demo.base.service.element.ElementService;
import com.example.demo.base.service.status.StatusService;
import com.example.demo.export.cim.CimExportService;
import com.example.demo.export.service.ExportService;

import java.util.List;

public class BaseAlgorithm extends AbstractAlgorithm<BasePowerNode, BaseLine, BaseConfiguration> {
    public BaseAlgorithm(Matrix<BasePowerNode> matrix,
                         ElementService<BasePowerNode, BaseLine> elementService,
                         StatusService<BasePowerNode> statusService,
                         ConnectionService<BasePowerNode> connectionService,
                         TopologyService<BasePowerNode, BaseLine> topologyService,
                         BaseConfiguration configuration,
                         PowerNodeFactory<BasePowerNode> nodeFactory,
                         ExportService<BasePowerNode, BaseLine> exportService,
                         CimExportService<BasePowerNode, BaseLine> cimExportService,
                         boolean randomFirst) {
        super(matrix,
            elementService,
            statusService,
            connectionService,
            topologyService,
            configuration,
            nodeFactory,
            exportService,
            cimExportService,
            randomFirst
        );
    }

    @Override
    protected BasePowerNode getBaseNode(int x, int y) {
        return new BasePowerNode(PowerNodeType.EMPTY, x, y, 0, List.of());
    }
}
