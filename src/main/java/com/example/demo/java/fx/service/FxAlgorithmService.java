package com.example.demo.java.fx.service;

import com.example.demo.base.algorithm.Algorithm;
import com.example.demo.base.model.configuration.GenerationConfiguration;
import com.example.demo.base.model.configuration.GenerationResult;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.TransformerConfiguration;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.export.service.AbstractExportService;
import com.example.demo.export.service.ExportService;
import com.example.demo.java.fx.algorithm.FxAlgorithm;
import com.example.demo.java.fx.factories.FxPowerNodeAbstractFactory;
import com.example.demo.java.fx.model.power.FxAbstractPowerNode;
import com.example.demo.java.fx.model.power.FxBaseNode;
import com.example.demo.java.fx.model.power.FxPowerLine;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.base.model.enums.VoltageLevel.LEVEL_10;
import static com.example.demo.base.model.enums.VoltageLevel.LEVEL_110;
import static com.example.demo.base.model.enums.VoltageLevel.LEVEL_220;
import static com.example.demo.base.model.enums.VoltageLevel.LEVEL_35;
import static com.example.demo.base.model.enums.VoltageLevel.LEVEL_500;


@RequiredArgsConstructor
public class FxAlgorithmService {

    private final int rows;
    private final int columns;
    private final int delays;
    private final int numberOfNodes;
    private final int numberOfEdges;


    public GenerationResult startAlgo(Group sceneParent) {
        FxConfiguration configuration = new FxConfiguration(rows, columns, 10_000, numberOfNodes, numberOfEdges, 2d, 4d, 4d, 45);

        Matrix<FxAbstractPowerNode> matrix = new Matrix<>(rows, columns);

        FxElementService elementService = fillGraphElements(sceneParent, configuration, matrix);

        fillMatrix(matrix, elementService);

        FxStatusService statusService = new FxStatusService(matrix, configuration, true);

        FxConnectionService connectionService = new FxConnectionService(elementService, configuration);

        FxPowerNodeAbstractFactory fabric = new FxPowerNodeAbstractFactory(elementService);

        ExportService<FxAbstractPowerNode, FxPowerLine> exportService = new AbstractExportService<>(configuration, elementService, matrix);

        List<TransformerConfiguration> transformerConfigurations = new ArrayList<>();

//        transformerConfigurations.add(TransformerConfiguration.builder()
//            .level(LEVEL_500).boundingAreaFrom(LEVEL_500.getBoundingArea())
//            .boundingAreaTo(LEVEL_500.getBoundingArea()+4)
//            .transformerPowerSet(List.of(10000))
//            .enabled(true)
//            .numberOfNodes(2)
//            .build());
//        transformerConfigurations.add(TransformerConfiguration.builder()
//            .level(LEVEL_220)
//            .boundingAreaFrom(LEVEL_220.getBoundingArea())
//            .boundingAreaTo(LEVEL_220.getBoundingArea() + 3)
//            .transformerPowerSet(List.of(5000))
//            .enabled(true)
//            .numberOfNodes(2)
//            .build());
        transformerConfigurations.add(TransformerConfiguration.builder()
            .level(LEVEL_110)
            .boundingAreaFrom(LEVEL_110.getBoundingArea())
            .boundingAreaTo(LEVEL_110.getBoundingArea() + 2)
            .transformerPowerSet(List.of(2500))
            .enabled(true)
            .numberOfNodes(1000)
            .build());
        transformerConfigurations.add(TransformerConfiguration.builder()
            .level(LEVEL_35)
            .boundingAreaFrom(LEVEL_35.getBoundingArea())
            .boundingAreaTo(LEVEL_35.getBoundingArea() + 1)
            .transformerPowerSet(List.of(1000))
            .enabled(true)
            .numberOfNodes(1000)
            .build());
        transformerConfigurations.add(TransformerConfiguration.builder()
            .level(LEVEL_10)
            .boundingAreaFrom(LEVEL_10.getBoundingArea())
            .boundingAreaTo(LEVEL_10.getBoundingArea() + 1)
            .transformerPowerSet(List.of(500))
            .enabled(true)
            .numberOfNodes(1000)
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
            .enabled(true)
            .build());
        loadConfigurations.add(LoadConfiguration.builder()
            .level(LEVEL_35)
            .minLoad(40)
            .maxLoad(70)
            .boundingArea(4)
            .transformerArea(3)
            .enabled(true)

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
            .boundingArea(LEVEL_220.getBoundingArea() - 2).transformerArea(3).build());
        generationConfigurations.add(GenerationConfiguration.builder()
            .level(LEVEL_500)
            .minPower(1000)
            .maxPower(3000)
            .boundingArea(LEVEL_500.getBoundingArea() - 2)
            .transformerArea(3)
            .build());

        Algorithm algorithm = new FxAlgorithm(matrix, elementService, statusService, connectionService, configuration, transformerConfigurations, loadConfigurations, generationConfigurations,
            fabric, exportService);

        GenerationResult generationResult = algorithm.start();

        return generationResult;
    }


    private void fillMatrix(Matrix<FxAbstractPowerNode> matrix, FxElementService elementService) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                FxAbstractPowerNode powerNode = new FxBaseNode(i, j, elementService.getBaseSize());
                matrix.fill(powerNode);

                int finalJ = j;
                int finalI = i;
                Platform.runLater(() -> {
                    GridPane.setConstraints(powerNode.getStackPane(), finalJ, finalI);
                    elementService.getGridPane().getChildren().add(powerNode.getStackPane());
                });
            }
        }
    }


    public FxElementService fillGraphElements(Group sceneParent, FxConfiguration cfg, Matrix<FxAbstractPowerNode> matrix) {
        GridPane gridPane = new GridPane();
        gridPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        gridPane.setGridLinesVisible(false);
        gridPane.setPadding(new Insets(cfg.getPadding()));
        gridPane.setVgap(cfg.getVGap());
        gridPane.setHgap(cfg.getHGap());

        Group root = new Group();
        Platform.runLater(() -> root.getChildren().add(gridPane));

        java.awt.Rectangle maximumWindowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        javafx.scene.control.ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefViewportHeight(maximumWindowBounds.getHeight() - 38);
        scrollPane.setPrefViewportWidth(maximumWindowBounds.getWidth() - 15);

        Platform.runLater(() -> scrollPane.setContent(root));

        Platform.runLater(() -> sceneParent.getChildren().add(0, scrollPane));

        return new FxElementService(matrix, cfg, scrollPane, root, gridPane);
    }

}