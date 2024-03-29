package com.example.demo.base.service;

import com.example.demo.base.algorithm.Algorithm;
import com.example.demo.base.algorithm.BaseAlgorithm;
import com.example.demo.base.factories.BasePowerNodeFactory;
import com.example.demo.base.factories.PowerNodeFactory;
import com.example.demo.base.model.configuration.GeneralResult;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.BaseLine;
import com.example.demo.base.model.power.BasePowerNode;
import com.example.demo.base.model.status.BaseBlockingStatusConfiguration;
import com.example.demo.base.service.connection.BaseConnectionService;
import com.example.demo.base.service.connection.ConnectionService;
import com.example.demo.base.service.element.BaseElementService;
import com.example.demo.base.service.status.BaseStatusService;
import com.example.demo.base.service.status.StatusService;
import com.example.demo.export.cim.BaseCimExportService;
import com.example.demo.export.cim.CimExportService;
import com.example.demo.export.service.BaseExportService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BaseAlgorithmService {

    private final int rows;
    private final int columns;
    private final int numberOfNodes;
    private final int numberOfEdges;

    public GeneralResult startAlgo() {
        BaseConfiguration configuration = new BaseConfiguration(rows, columns, numberOfNodes, numberOfEdges);

        Matrix<BasePowerNode> matrix = new Matrix<>(rows, columns);
        fillMatrix(matrix);

        BaseElementService elementService = new BaseElementService(matrix);

        StatusService<BasePowerNode> statusService = new BaseStatusService(matrix, configuration);

        TopologyService<BasePowerNode, BaseLine> topologyService = new BaseTopologyService<>(elementService);

        ConnectionService<BasePowerNode> connectionService = new BaseConnectionService(elementService, configuration, topologyService);

        PowerNodeFactory<BasePowerNode> factory = new BasePowerNodeFactory();

        BaseExportService exportService = new BaseExportService(configuration, elementService, matrix);

        CimExportService<BasePowerNode, BaseLine> cimExportService = new BaseCimExportService<>(
            ConfigurationStaticSupplier.cimExportProportionalityFactor,
            ConfigurationStaticSupplier.cimExportInitialXOffset,
            ConfigurationStaticSupplier.cimExportInitialYOffset,
            configuration,
            elementService);

        configuration.setTransformerConfigurations(ConfigurationStaticSupplier.transformerConfigurations);
        configuration.setLoadConfigurations(ConfigurationStaticSupplier.loadConfigurations);
        configuration.setGeneratorConfigurations(ConfigurationStaticSupplier.generatorConfigurations);
        configuration.setBaseBlockingStatusConfiguration(new BaseBlockingStatusConfiguration(ConfigurationStaticSupplier.baseBlockingStatusRoundedArea, ConfigurationStaticSupplier.baseBlockingStatusBoundingAreaRadius));

        Algorithm algorithm = new BaseAlgorithm(
            matrix,
            elementService,
            statusService,
            connectionService,
            topologyService,
            configuration,
            factory,
            exportService,
            cimExportService,
            ConfigurationStaticSupplier.randomFirstNode
        );
        GeneralResult result = algorithm.start();

        return result;
    }

    private void fillMatrix(Matrix<BasePowerNode> matrix) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                BasePowerNode powerNode = new BasePowerNode(PowerNodeType.EMPTY, i, j, 0, List.of());
                matrix.fill(powerNode);
            }
        }
    }
}
