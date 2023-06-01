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
import com.example.demo.base.model.power.BasePowerNode;
import com.example.demo.base.service.connection.BaseConnectionService;
import com.example.demo.base.service.connection.ConnectionService;
import com.example.demo.base.service.element.BaseElementService;
import com.example.demo.base.service.status.BaseStatusService;
import com.example.demo.base.service.status.StatusService;
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

        ConnectionService<BasePowerNode> connectionService = new BaseConnectionService(elementService, configuration);

        PowerNodeFactory<BasePowerNode> factory = new BasePowerNodeFactory();

        BaseExportService exportService = new BaseExportService(configuration, elementService, matrix);

        List<TransformerConfiguration> transformerConfigurations = new ArrayList<>();

        transformerConfigurations.add(TransformerConfiguration.builder()
            .level(LEVEL_500)
            .boundingAreaFrom(LEVEL_500.getBoundingArea())
            .boundingAreaTo(LEVEL_500.getBoundingArea() + 4)
            .maxLineLength(LEVEL_500.getBoundingArea() + 4)
            .transformerPowerSet(List.of(10000))
            .enabled(false)
            .numberOfNodes(2)
            .maxChainLength(3)
            .build());
        transformerConfigurations.add(TransformerConfiguration.builder()
            .level(LEVEL_220)
            .boundingAreaFrom(LEVEL_220.getBoundingArea())
            .boundingAreaTo(LEVEL_220.getBoundingArea() + 3)
            .maxLineLength(LEVEL_220.getBoundingArea() + 3)
            .transformerPowerSet(List.of(5000))
            .enabled(false)
            .numberOfNodes(3)
            .maxChainLength(3)
            .build());
        transformerConfigurations.add(TransformerConfiguration.builder()
            .level(LEVEL_110)
            .boundingAreaFrom(LEVEL_110.getBoundingArea())
            .boundingAreaTo(LEVEL_110.getBoundingArea() + 2)
            .maxLineLength(LEVEL_110.getBoundingArea() + 2)
            .transformerPowerSet(List.of(2500))
            .enabled(true)
            .numberOfNodes(2)
            .maxChainLength(3)
            .build());
        transformerConfigurations.add(TransformerConfiguration.builder()
            .level(LEVEL_35)
            .boundingAreaFrom(LEVEL_35.getBoundingArea())
            .boundingAreaTo(LEVEL_35.getBoundingArea() + 1)
            .maxLineLength(LEVEL_35.getBoundingArea() + 1)
            .transformerPowerSet(List.of(1000))
            .enabled(true)
            .numberOfNodes(11)
            .maxChainLength(3)
            .build());
        transformerConfigurations.add(TransformerConfiguration.builder()
            .level(LEVEL_10)
            .boundingAreaFrom(LEVEL_10.getBoundingArea())
            .boundingAreaTo(LEVEL_10.getBoundingArea() + 1)
            .maxLineLength(LEVEL_10.getBoundingArea() + 1)
            .transformerPowerSet(List.of(500))
            .enabled(true)
            .numberOfNodes(1000)
            .maxChainLength(3)
            .build());

        List<LoadConfiguration> loadConfigurations = new ArrayList<>();
        // Трансформаторы напряжением 35/10 кВ имеют следующий ряд мощностей 1000, 1600, 2500, 4000, 6300
        // http://kabelmag2012.narod.ru/TransfS.html
        loadConfigurations.add(LoadConfiguration.builder()
            .level(LEVEL_10)
            .minLoad(10)
            .maxLoad(20)
            .boundingAreaFrom(2)
            .boundingAreaTo(4)
            .maxLineLength(4)
            .maxChainLength(5)
            .enabled(true)
            .generationRate(100)
            .build());
        loadConfigurations.add(LoadConfiguration.builder()
            .level(LEVEL_35)
            .minLoad(40)
            .maxLoad(70)
            .boundingAreaFrom(2)
            .boundingAreaTo(4)
            .maxLineLength(4)
            .maxChainLength(1)
            .enabled(true)
            .generationRate(40)
            .build());

        List<GeneratorConfiguration> generatorConfigurations = new ArrayList<>();
        generatorConfigurations.add(GeneratorConfiguration.builder()
            .level(LEVEL_35)
            .minPower(200)
            .maxPower(400)
            .boundingArea(LEVEL_35.getBoundingArea() - 4)
            .transformerArea(2)
            .enabled(true)
            .build());
        generatorConfigurations.add(GeneratorConfiguration.builder()
            .level(LEVEL_110)
            .minPower(500)
            .maxPower(800)
            .boundingArea(LEVEL_110.getBoundingArea() - 4)
            .transformerArea(3)
            .enabled(true)
            .build());
        generatorConfigurations.add(GeneratorConfiguration.builder()
            .level(LEVEL_220)
            .minPower(700)
            .maxPower(1000)
            .boundingArea(LEVEL_220.getBoundingArea() - 2).transformerArea(3)
            .enabled(false)
            .build());
        generatorConfigurations.add(GeneratorConfiguration.builder()
            .level(LEVEL_500)
            .minPower(1000)
            .maxPower(3000)
            .boundingArea(LEVEL_500.getBoundingArea() - 2)
            .transformerArea(3)
            .enabled(false)
            .build());

        configuration.setTransformerConfigurations(transformerConfigurations);
        configuration.setLoadConfigurations(loadConfigurations);
        configuration.setGeneratorConfigurations(generatorConfigurations);

        Algorithm algorithm = new BaseAlgorithm(matrix, elementService, statusService, connectionService, configuration, transformerConfigurations, loadConfigurations,
            generatorConfigurations,
            factory, exportService, false);
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
