package com.example.demo.java.fx.service;

import com.example.demo.base.algorithm.Algorithm;
import com.example.demo.base.factories.PowerNodeFactory;
import com.example.demo.base.model.configuration.GeneralResult;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.service.BaseTopologyService;
import com.example.demo.base.service.ConfigurationStaticSupplier;
import com.example.demo.base.service.TopologyService;
import com.example.demo.base.service.connection.ConnectionService;
import com.example.demo.base.service.status.StatusService;
import com.example.demo.export.cim.BaseCimExportService;
import com.example.demo.export.cim.CimExportService;
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
import java.util.Arrays;


@RequiredArgsConstructor
public class FxAlgorithmService {

    private final int rows;
    private final int columns;
    private final int numberOfNodes;
    private final int numberOfEdges;


    public GeneralResult startAlgo(Group sceneParent) {
        FxConfiguration configuration = new FxConfiguration(rows, columns, numberOfNodes, numberOfEdges, 2d, 4d, 4d, 45);

        Matrix<FxAbstractPowerNode> matrix = new Matrix<>(rows, columns);

        FxElementService elementService = fillGraphElements(sceneParent, configuration, matrix);

        fillMatrix(matrix, elementService);

        StatusService<FxAbstractPowerNode> statusService = new FxStatusService(matrix, configuration, true);

        TopologyService<FxAbstractPowerNode, FxPowerLine> topologyService = new BaseTopologyService<>(elementService);

        ConnectionService<FxAbstractPowerNode> connectionService = new FxConnectionService(elementService, configuration, topologyService);

        PowerNodeFactory<FxAbstractPowerNode> fabric = new FxPowerNodeAbstractFactory(elementService);

        ExportService<FxAbstractPowerNode, FxPowerLine> exportService = new AbstractExportService<>(configuration, elementService, matrix);

        CimExportService<FxAbstractPowerNode, FxPowerLine> cimExportService = new BaseCimExportService<>(configuration, elementService);

        configuration.setTransformerConfigurations(ConfigurationStaticSupplier.getTransformerConfigurations());
        configuration.setLoadConfigurations(ConfigurationStaticSupplier.getLoadConfigurations());
        configuration.setGeneratorConfigurations(ConfigurationStaticSupplier.getGeneratorConfigurations());

        Algorithm algorithm = new FxAlgorithm(
            matrix,
            elementService,
            statusService,
            connectionService,
            topologyService,
            configuration,
            fabric,
            exportService,
            cimExportService,
            true
        );

        GeneralResult generalResult = null;
        try {
            generalResult = algorithm.start();
        } catch (Exception e) {
            System.out.println(e + "\n" + Arrays.toString(e.getStackTrace()));
        }

        return generalResult;
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
