package com.example.demo;

import com.example.demo.dto.PowerLineDto;
import com.example.demo.dto.PowerNodeDto;
import com.example.demo.dto.SaveDto;
import com.example.demo.model.Matrix;
import com.example.demo.model.power.node.BaseNode;
import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.PowerNodeType;
import com.example.demo.procedure.AbstractNodeFabric;
import com.example.demo.services.Configuration;
import com.example.demo.services.ConnectionService;
import com.example.demo.services.ConnectionServiceImpl;
import com.example.demo.services.ElementServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.io.FileReader;
import java.util.Optional;

public class DeserializeMain extends Application {

    static SaveDto saveDto;
    static Configuration cfg;
    static Matrix<PowerNode> matrix;
    static GridPane gridPane;
    static ElementServiceImpl elementService;


    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        String path = "C:\\Users\\mnikitin\\IdeaProjects\\other\\demo\\src\\main\\resources\\schemes\\scheme_15_04_2023__11_00_27";
        try (FileReader reader = new FileReader(path)) {
            saveDto = objectMapper.readValue(reader, SaveDto.class);
        } catch (Exception e) {
            System.out.println("Exception : " + e);
        }
        System.out.println("Parsing successfully ends");
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        int rows = saveDto.getRows();
        int columns = saveDto.getColumns();
        cfg = new Configuration(rows, columns);
        matrix = new Matrix<>(rows, columns);

        fillGraphElements(stage, cfg);
        fillMatrix(rows, columns, elementService);

        ConnectionService connectionService = new ConnectionServiceImpl(elementService);
        AbstractNodeFabric fabric = new AbstractNodeFabric(elementService);

        // Расстановка node-ов по карте
        for (PowerNodeDto nodeDto : saveDto.getMatrix()) {
            if (PowerNodeType.EMPTY.equals(nodeDto.getNodeType())) {
                continue;
            }

            PowerNode node = fabric.createNode(nodeDto.getNodeType(), nodeDto.getX(), nodeDto.getY(), nodeDto.getPower(), nodeDto.getVoltageLevels());
            node.setUuid(nodeDto.getUuid());

            elementService.addPowerNodeToGrid(node);
        }

        // Нанесение линий электропередачи на карту
        // TODO не работает
        for (PowerLineDto line : saveDto.getLines()) {
            Optional<PowerNode> point1 = matrix.getNode(line.getPoint1().getX(), line.getPoint1().getY());
            Optional<PowerNode> point2 = matrix.getNode(line.getPoint2().getX(), line.getPoint2().getY());
            connectionService.connectNodes(point1.get(), point2.get(), line.getVoltageLevel());
        }


        stage.show();
    }

    private static void fillMatrix(int rows, int columns, ElementServiceImpl elementService) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                PowerNode powerNode = new BaseNode(elementService.getBaseSize());
                powerNode.setX(i);
                powerNode.setY(j);
                matrix.fill(powerNode);
                GridPane.setConstraints(powerNode.getStackPane(), j, i);
                gridPane.getChildren().add(powerNode.getStackPane());
            }
        }
    }

    public static void fillGraphElements(Stage stage, Configuration cfg) {
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

        elementService = new ElementServiceImpl(cfg, stage, scene, scrollPane, root, gridPane, matrix);
    }

}
