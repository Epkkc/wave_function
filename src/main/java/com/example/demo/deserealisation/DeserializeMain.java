package com.example.demo.deserealisation;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.deserealisation.service.DeserializationService;
import com.example.demo.export.dto.PowerLineDto;
import com.example.demo.export.dto.PowerNodeDto;
import com.example.demo.export.dto.SaveDto;
import com.example.demo.java.fx.factories.FxAbstractPowerNodeFactory;
import com.example.demo.java.fx.model.power.FxBaseNode;
import com.example.demo.java.fx.model.power.FxPowerNode;
import com.example.demo.java.fx.service.FxConfiguration;
import com.example.demo.services.FxConnectionService;
import com.example.demo.services.FxElementService;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Optional;

public class DeserializeMain extends Application {

    static SaveDto saveDto;
    static FxConfiguration cfg;
    static Matrix<FxPowerNode> matrix;
    static GridPane gridPane;
    static FxElementService elementService;
    static DeserializationService deserializationService = new DeserializationService();

    static String path = "C:\\Users\\mnikitin\\IdeaProjects\\other\\demo\\src\\main\\resources\\schemes\\";
    static String fileName = "scheme_17_04_2023__08_06_07";

    public static void main(String[] args) {
        saveDto = deserializationService.extractSaveDto(path + fileName);
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        int rows = saveDto.getRows();
        int columns = saveDto.getColumns();
        cfg = new FxConfiguration(rows, columns, 10_000, 2d, 4d, 4d, 45);
        matrix = new Matrix<>(rows, columns);

        fillGraphElements(stage, cfg);
        fillMatrix(rows, columns, elementService);

        FxConnectionService connectionService = new FxConnectionService(elementService);
        FxAbstractPowerNodeFactory fabric = new FxAbstractPowerNodeFactory(elementService);

        stage.show();


        // Расстановка node-ов по карте
        for (PowerNodeDto nodeDto : saveDto.getMatrix()) {
            if (PowerNodeType.EMPTY.equals(nodeDto.getNodeType())) {
                continue;
            }

            FxPowerNode node = fabric.createNode(nodeDto.getNodeType(), nodeDto.getX(), nodeDto.getY(), nodeDto.getPower(), nodeDto.getVoltageLevels());
            node.setUuid(nodeDto.getUuid());

            elementService.addPowerNodeToGrid(node);
        }

        // Нанесение линий электропередачи на карту
        // TODO не работает
        for (PowerLineDto line : saveDto.getLines()) {
            Optional<FxPowerNode> point1 = matrix.getNode(line.getPoint1().getX(), line.getPoint1().getY());
            Optional<FxPowerNode> point2 = matrix.getNode(line.getPoint2().getX(), line.getPoint2().getY());
            connectionService.connectNodes(point1.get(), point2.get(), line.getVoltageLevel());
        }


    }

    private static void fillMatrix(int rows, int columns, FxElementService elementService) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                FxPowerNode powerNode = new FxBaseNode(i, j, elementService.getBaseSize());
                matrix.fill(powerNode);
                GridPane.setConstraints(powerNode.getStackPane(), j, i);
                gridPane.getChildren().add(powerNode.getStackPane());
            }
        }
    }

    public static void fillGraphElements(Stage stage, FxConfiguration cfg) {
        gridPane = new GridPane();
        gridPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        gridPane.setGridLinesVisible(false);
        gridPane.setPadding(new Insets(cfg.getPadding()));
        gridPane.setVgap(cfg.getVGap());
        gridPane.setHgap(cfg.getHGap());

        Rectangle maximumWindowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        Group sceneRoot = new Group();

        javafx.scene.control.ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefViewportHeight(maximumWindowBounds.getHeight() - 38);
        scrollPane.setPrefViewportWidth(maximumWindowBounds.getWidth() - 15);

        sceneRoot.getChildren().add(scrollPane);

        Scene scene = new Scene(sceneRoot, Color.WHITE); // 969faf
        scene.setFill(Color.WHITE);

        Group root = new Group();

        root.getChildren().add(gridPane);

        scrollPane.setContent(root);

        stage.setTitle("Wavefunction Collapse Algorithm");
        stage.getIcons().add(new Image("C:\\Users\\mnikitin\\IdeaProjects\\other\\demo\\src\\main\\resources\\com\\example\\demo\\icon.png"));
        stage.setScene(scene);
        stage.setMaximized(true);

        elementService = new FxElementService(cfg, stage, scene, scrollPane, root, gridPane, matrix);
    }

}
