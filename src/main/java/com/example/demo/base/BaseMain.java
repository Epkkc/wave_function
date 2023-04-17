package com.example.demo.base;

import com.example.demo.base.algorithm.Algorithm;
import com.example.demo.base.algorithm.BaseAlgorithm;
import com.example.demo.base.factories.BasePowerNodeFactory;
import com.example.demo.base.factories.PowerNodeFactory;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.VoltageLevelInfo;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.BasePowerNode;
import com.example.demo.base.service.BaseConfiguration;
import com.example.demo.base.service.BaseElementService;
import com.example.demo.base.service.BaseStatusService;
import com.example.demo.base.service.ConnectionService;
import com.example.demo.base.service.StatusService;
import com.example.demo.export.service.ExportService;
import com.example.demo.base.service.BaseConnectionService;
import com.example.demo.java.fx.model.power.FxBaseNode;
import com.example.demo.java.fx.model.power.FxPowerNode;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.base.model.enums.VoltageLevel.LEVEL_10;
import static com.example.demo.base.model.enums.VoltageLevel.LEVEL_110;
import static com.example.demo.base.model.enums.VoltageLevel.LEVEL_220;
import static com.example.demo.base.model.enums.VoltageLevel.LEVEL_35;

public class BaseMain {

    static int rows = 30;
    static int columns = 30;
    static int delays = 2000;

    public static void main(String[] args) {

        BaseConfiguration configuration = new BaseConfiguration(rows, columns, delays);

        Matrix<BasePowerNode> matrix = new Matrix<>(rows, columns);
        fillMatrix(matrix);

        BasePowerNodeFactory nodeFactory = new BasePowerNodeFactory();

        BaseElementService elementService = new BaseElementService(matrix);

        StatusService statusService = new BaseStatusService(matrix, true);

        ConnectionService connectionService = new BaseConnectionService(elementService);

        PowerNodeFactory<BasePowerNode> factory = new BasePowerNodeFactory();

        ExportService exportService = new ExportService(configuration, elementService, matrix);

        List<VoltageLevelInfo> voltageLevels = new ArrayList<>();

        // TODO добавлять сюда в зависимости от положения чекбокса (VoltageLevelInfo.enabled)
        // TODO также заполнять boundingArea теми значениями, которые заполнит пользователь
//        voltageLevels.add(VoltageLevelInfo.builder().level(LEVEL_500).boundingAreaFrom(LEVEL_500.getBoundingArea()).boundingAreaTo(LEVEL_500.getBoundingArea()+4).transformerPowerSet(List.of(10000)).build());
        voltageLevels.add(VoltageLevelInfo.builder()
            .level(LEVEL_220)
            .boundingAreaFrom(LEVEL_220.getBoundingArea())
            .boundingAreaTo(LEVEL_220.getBoundingArea() + 3)
            .transformerPowerSet(List.of(5000))
            .build());
        voltageLevels.add(VoltageLevelInfo.builder()
            .level(LEVEL_110)
            .boundingAreaFrom(LEVEL_110.getBoundingArea())
            .boundingAreaTo(LEVEL_110.getBoundingArea() + 2)
            .transformerPowerSet(List.of(2500))
            .build());
        voltageLevels.add(VoltageLevelInfo.builder()
            .level(LEVEL_35)
            .boundingAreaFrom(LEVEL_35.getBoundingArea())
            .boundingAreaTo(LEVEL_35.getBoundingArea() + 1)
            .transformerPowerSet(List.of(1000))
            .build());
        voltageLevels.add(
            VoltageLevelInfo.builder().level(LEVEL_10).boundingAreaFrom(LEVEL_10.getBoundingArea()).boundingAreaTo(LEVEL_10.getBoundingArea() + 1).transformerPowerSet(List.of(500)).build());

        List<LoadConfiguration> loadConfigurations = new ArrayList<>();
        // Трансформаторы напряжением 35/10 кВ имеют следующий ряд мощностей 1000, 1600, 2500, 4000, 6300
        // http://kabelmag2012.narod.ru/TransfS.html
        loadConfigurations.add(LoadConfiguration.builder().level(LEVEL_10).minLoad(40).maxLoad(200).boundingArea(2).build());
        loadConfigurations.add(LoadConfiguration.builder().level(LEVEL_35).minLoad(300).maxLoad(500).boundingArea(3).build());



        Algorithm algorithm = new BaseAlgorithm<BasePowerNode>(matrix, elementService, statusService, connectionService, configuration, voltageLevels, loadConfigurations, factory, exportService);
        algorithm.start();

    }

    static void fillMatrix(Matrix<BasePowerNode> matrix) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                BasePowerNode powerNode = new BasePowerNode(PowerNodeType.EMPTY ,i, j, 0, List.of());
                matrix.fill(powerNode);
            }
        }
    }

}
