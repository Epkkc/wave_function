package com.example.demo.base.service;

import com.example.demo.base.algorithm.Algorithm;
import com.example.demo.base.algorithm.BaseAlgorithm;
import com.example.demo.base.factories.BasePowerNodeFactory;
import com.example.demo.base.factories.PowerNodeFactory;
import com.example.demo.base.model.configuration.GenerationConfiguration;
import com.example.demo.base.model.configuration.GenerationResult;
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
    private final int delays; // todo удалить delay отсюда и из алгоритма, сделать для него соответствующие хуки
    private final int numberOfNodes;
    private final int numberOfEdges;

    public GenerationResult startAlgo() {
        BaseConfiguration configuration = new BaseConfiguration(rows, columns, delays, numberOfNodes, numberOfEdges);

        Matrix<BasePowerNode> matrix = new Matrix<>(rows, columns);
        fillMatrix(matrix);

        BaseElementService elementService = new BaseElementService(matrix);

        StatusService<BasePowerNode> statusService = new BaseStatusService(matrix, configuration, true);

        ConnectionService<BasePowerNode> connectionService = new BaseConnectionService(elementService);

        PowerNodeFactory<BasePowerNode> factory = new BasePowerNodeFactory();

        BaseExportService exportService = new BaseExportService(configuration, elementService, matrix);

        List<TransformerConfiguration> transformerConfigurations = new ArrayList<>();

        // TODO добавлять сюда в зависимости от положения чекбокса (VoltageLevelInfo.enabled)
        // TODO также заполнять boundingArea теми значениями, которые заполнит пользователь
        transformerConfigurations.add(TransformerConfiguration.builder()
            .level(LEVEL_500)
            .boundingAreaFrom(LEVEL_500.getBoundingArea())
            .boundingAreaTo(LEVEL_500.getBoundingArea() + 4)
            .transformerPowerSet(List.of(10000))
            .enabled(false)
            .numberOfNodes(2)
            .build());
        transformerConfigurations.add(TransformerConfiguration.builder()
            .level(LEVEL_220)
            .boundingAreaFrom(LEVEL_220.getBoundingArea())
            .boundingAreaTo(LEVEL_220.getBoundingArea() + 3)
            .transformerPowerSet(List.of(5000))
            .enabled(true)
            .numberOfNodes(3)
            .build());
        transformerConfigurations.add(TransformerConfiguration.builder()
            .level(LEVEL_110)
            .boundingAreaFrom(LEVEL_110.getBoundingArea())
            .boundingAreaTo(LEVEL_110.getBoundingArea() + 2)
            .transformerPowerSet(List.of(2500))
            .enabled(true)
            .numberOfNodes(6)
            .build());
        transformerConfigurations.add(TransformerConfiguration.builder()
            .level(LEVEL_35)
            .boundingAreaFrom(LEVEL_35.getBoundingArea())
            .boundingAreaTo(LEVEL_35.getBoundingArea() + 1)
            .transformerPowerSet(List.of(1000))
            .enabled(true)
            .numberOfNodes(15)
            .build());
        transformerConfigurations.add(TransformerConfiguration.builder()
            .level(LEVEL_10)
            .boundingAreaFrom(LEVEL_10.getBoundingArea())
            .boundingAreaTo(LEVEL_10.getBoundingArea() + 1)
            .transformerPowerSet(List.of(500))
            .enabled(true)
            .numberOfNodes(20)
            .build());

        List<LoadConfiguration> loadConfigurations = new ArrayList<>();
        // Трансформаторы напряжением 35/10 кВ имеют следующий ряд мощностей 1000, 1600, 2500, 4000, 6300
        // http://kabelmag2012.narod.ru/TransfS.html
        loadConfigurations.add(LoadConfiguration.builder()
            .level(LEVEL_10)
            .minLoad(10)
            .maxLoad(20)
            .boundingArea(3)
            .transformerArea(2)
            .build());
        loadConfigurations.add(LoadConfiguration.builder()
            .level(LEVEL_35)
            .minLoad(40)
            .maxLoad(70)
            .boundingArea(4)
            .transformerArea(3)
            .build());

        List<GenerationConfiguration> generationConfigurations = new ArrayList<>();
        generationConfigurations.add(GenerationConfiguration.builder()
            .level(LEVEL_110)
            .minPower(200)
            .maxPower(300)
            .boundingArea(LEVEL_110.getBoundingArea() - 2)
            .transformerArea(2)
            .build());
        generationConfigurations.add(GenerationConfiguration.builder()
            .level(LEVEL_220)
            .minPower(500)
            .maxPower(1000)
            .boundingArea(LEVEL_220.getBoundingArea() - 2)
            .transformerArea(3).build());
        generationConfigurations.add(GenerationConfiguration.builder()
            .level(LEVEL_500)
            .minPower(1000)
            .maxPower(3000)
            .boundingArea(LEVEL_500.getBoundingArea() - 2)
            .transformerArea(3).build());

        configuration.setTransformerConfigurations(transformerConfigurations);
        configuration.setLoadConfigurations(loadConfigurations);
        configuration.setGenerationConfigurations(generationConfigurations);

        Algorithm algorithm = new BaseAlgorithm(matrix, elementService, statusService, connectionService, configuration, transformerConfigurations, loadConfigurations,
            generationConfigurations,
            factory, exportService);
        GenerationResult result = algorithm.start();

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
