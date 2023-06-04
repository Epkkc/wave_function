package com.example.demo.base.service;

import com.example.demo.base.algorithm.Algorithm;
import com.example.demo.base.algorithm.BaseAlgorithm;
import com.example.demo.base.factories.BasePowerNodeFactory;
import com.example.demo.base.factories.PowerNodeFactory;
import com.example.demo.base.model.configuration.GeneralResult;
import com.example.demo.base.model.configuration.GeneratorConfiguration;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.TransformerConfiguration;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.BaseLine;
import com.example.demo.base.model.power.BasePowerNode;
import com.example.demo.base.service.connection.BaseConnectionService;
import com.example.demo.base.service.connection.ConnectionService;
import com.example.demo.base.service.element.BaseElementService;
import com.example.demo.base.service.status.BaseStatusService;
import com.example.demo.base.service.status.StatusService;
import com.example.demo.export.cim.CimExportService;
import com.example.demo.export.service.BaseExportService;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.base.model.enums.VoltageLevel.LEVEL_10;
import static com.example.demo.base.model.enums.VoltageLevel.LEVEL_110;
import static com.example.demo.base.model.enums.VoltageLevel.LEVEL_220;
import static com.example.demo.base.model.enums.VoltageLevel.LEVEL_35;
import static com.example.demo.base.model.enums.VoltageLevel.LEVEL_500;

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

        StatusService<BasePowerNode> statusService = new BaseStatusService(matrix, configuration, true);

        TopologyService<BasePowerNode, BaseLine> topologyService = new BaseTopologyService<>(elementService);

        ConnectionService<BasePowerNode> connectionService = new BaseConnectionService(elementService, configuration, topologyService);

        PowerNodeFactory<BasePowerNode> factory = new BasePowerNodeFactory();

        BaseExportService exportService = new BaseExportService(configuration, elementService, matrix);

        CimExportService<BasePowerNode, BaseLine> cimExportService = new CimExportService<>(configuration, elementService);

        configuration.setTransformerConfigurations(ConfigurationStaticSupplier.getTransformerConfigurations());
        configuration.setLoadConfigurations(ConfigurationStaticSupplier.getLoadConfigurations());
        configuration.setGeneratorConfigurations(ConfigurationStaticSupplier.getGeneratorConfigurations());

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
            false
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
