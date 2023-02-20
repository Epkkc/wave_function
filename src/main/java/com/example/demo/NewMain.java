package com.example.demo;

import com.example.demo.model.Matrix;
import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.status.StatusSupplier;
import com.example.demo.model.status.StatusSupplierImpl;
import com.example.demo.services.Algorithm;
import com.example.demo.services.Configuration;
import com.example.demo.services.ConnectionService;
import com.example.demo.services.ConnectionServiceImpl;
import com.example.demo.services.DecisionMaker;
import com.example.demo.services.ElementServiceImpl;
import com.example.demo.services.FilterServiceImpl;
import com.example.demo.services.StatusService;
import com.example.demo.thread.StoppableThread;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;

public class NewMain extends Application {

    static int rows = 10;
    static int columns = 10;
    static StoppableThread thread;
    static Configuration cfg;
    static ElementServiceImpl elementService;
    static Matrix<PowerNode> matrix;
    static GridPane gridPane;


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {

        cfg = new Configuration(rows, columns);

        // TODO заполнить матрицу
        matrix = new Matrix<>(rows, columns);

        fillGraphElements(stage, cfg);

        fillMatrix();

        StatusSupplier statusSupplier = new StatusSupplierImpl();

        FilterServiceImpl filterService = new FilterServiceImpl();

        DecisionMaker decisionMaker = new DecisionMaker(elementService);

        StatusService statusService = new StatusService(matrix, statusSupplier, true);

        ConnectionService connectionService = new ConnectionServiceImpl(elementService);

        Algorithm algorithm = new Algorithm(matrix, statusSupplier, filterService, decisionMaker, elementService, statusService, connectionService, cfg);


        thread = new StoppableThread(algorithm::startAlgo);
        thread.setDaemon(true);

        stage.show();

        thread.start();

    }

    private static void fillMatrix() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                PowerNode powerNode = new PowerNode(elementService.getBaseSize());
                powerNode.setX(i);
                powerNode.setY(j);
                matrix.fill(powerNode);
                GridPane.setConstraints(powerNode.getStackPane(), j, i);
                gridPane.getChildren().add(powerNode.getStackPane());
            }
        }
    }

    private static void fillGraphElements(Stage stage, Configuration cfg) {
        gridPane = new GridPane();
        gridPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        gridPane.setGridLinesVisible(false);
        gridPane.setPadding(new Insets(cfg.getPadding()));
        gridPane.setVgap(cfg.getVGap());
        gridPane.setHgap(cfg.getHGap());

        Rectangle maximumWindowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        javafx.scene.control.ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefViewportHeight(maximumWindowBounds.getHeight() - 38);
        scrollPane.setPrefViewportWidth(maximumWindowBounds.getWidth() - 15);

        Scene scene = new Scene(scrollPane, Color.WHITE); // 969faf
        scene.setFill(Color.WHITE);

        Group root = new Group();

        root.getChildren().add(gridPane);

        scrollPane.setContent(root);

        stage.setTitle("Wavefunction Collapse Algorithm");
        stage.getIcons().add(new Image("C:\\Users\\mnikitin\\IdeaProjects\\other\\demo\\src\\main\\resources\\com\\example\\demo\\icon.png"));
        stage.setScene(scene);
        stage.setMaximized(true);

        scene.setOnKeyPressed(keyEvent -> {
            if (KeyCode.CAPS.equals(keyEvent.getCode())) {
                thread.changeStopped();
                System.out.println("Нажатие на SPACE");
                System.out.println("Thread 1 stopped = " + thread.isStopped());
                // TODO вывести подсказку снизу экрана, которая бы затухала через несколько секунд
            }
        });

        elementService = new ElementServiceImpl(cfg, stage, scene, scrollPane, root, gridPane, matrix);
    }

}
