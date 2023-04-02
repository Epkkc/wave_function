package com.example.demo;

import com.example.demo.model.Matrix;
import com.example.demo.model.power.node.BaseNode;
import com.example.demo.model.power.node.Generator;
import com.example.demo.model.power.node.Load;
import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.VoltageLevel;
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
import javafx.application.Platform;
import javafx.geometry.Bounds;
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
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;

public class NewMain extends Application {

    static int rows = 60;
    static int columns = 60;
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


        PowerNode generator = new Generator(elementService.getBaseSize(), VoltageLevel.LEVEL_220);
        GridPane.setConstraints(generator.getStackPane(), 0, rows + 1);
        gridPane.getChildren().add(generator.getStackPane());

        PowerNode load = new Load(elementService.getBaseSize(), VoltageLevel.LEVEL_110);
        GridPane.setConstraints(load.getStackPane(), 1, rows + 1);
        gridPane.getChildren().add(load.getStackPane());

    }

    private static void fillMatrix() {
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

    private static void fillGraphElements(Stage stage, Configuration cfg) {
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

        StackPane stopMessage = getStopMessageBlock("Для продолжения работы нажмите CAPS LOCK");
        stopMessage.setAlignment(Pos.BOTTOM_CENTER);
        stopMessage.setOpacity(0);
        stopMessage.mouseTransparentProperty().set(true);

        sceneRoot.getChildren().add(stopMessage);
        scene.setOnKeyPressed(keyEvent -> {
            if (KeyCode.CAPS.equals(keyEvent.getCode())) {
                thread.changeStopped();
                System.out.println("Нажатие на CAPS");
                System.out.println("Thread 1 stopped = " + thread.isStopped());
                if (thread.isStopped()) {
                    stopMessage.setOpacity(0.85);
                } else {
                    stopMessage.setOpacity(0);
                }
            }
        });

        elementService = new ElementServiceImpl(cfg, stage, scene, scrollPane, root, gridPane, matrix);
    }

    private static StackPane getStopMessageBlock(String message) {
        int hPadding = 10;
        int vPadding = 7;

        Text text = new Text();
        text.setFont(Font.getDefault());
        text.setText(message);

        StackPane stackPane = new StackPane();
        stackPane.setPadding(new Insets(vPadding, hPadding, vPadding, hPadding));
        stackPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85);");

        stackPane.getChildren().add(text);

        return stackPane;
    }

}
