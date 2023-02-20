package com.example.demo;

import com.example.demo.model.power.node.ConnectionPoint;
import com.example.demo.model.Matrix;
import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.PowerNodeType;
import com.example.demo.model.status.BlockType;
import com.example.demo.model.status.StatusType;
import com.example.demo.thread.StoppableThread;
import com.example.demo.model.power.node.SubStation;
import com.example.demo.model.power.node.VoltageLevel;
import com.example.demo.services.ElementsService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
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
import javafx.scene.shape.Line;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static java.lang.Math.*;

public class Main extends Application {

    public static void main(String[] args) {
        launch();
    }

    private static final ElementsService elementsService = new ElementsService();

    public static Group root;


    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        double padding = 2d;
        double vGap = 4d;
        double hGap = 4d;


        GridPane gridPane = new GridPane();
        gridPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        gridPane.setGridLinesVisible(false);
        gridPane.setPadding(new Insets(padding));
        gridPane.setVgap(vGap);
        gridPane.setHgap(hGap);

        int rows = 10; // 18
        int columns = 20; // 35
        double size = 120;


        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        double minSizeByRows = (bounds.getHeight() - 2 * padding - (rows - 1) * vGap - 35) / rows;
        double minSizeByColumns = (bounds.getWidth() - 2 * padding - (columns - 1) * hGap - 35) / columns;
//        double size = Math.min(minSizeByRows, minSizeByColumns);


//        Matrix<StackPane> matrix = new Matrix<>(rows, columns);

//        fillGridPaneAndMatrix(matrix, gridPane, rows, columns, size);

        Matrix<PowerNode> matrix = new Matrix<>(rows, columns);

        fillPowerGridPaneAndMatrix(matrix, gridPane, rows, columns, size);

        Rectangle maximumWindowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();


        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefViewportHeight(maximumWindowBounds.getHeight() - 38);
        scrollPane.setPrefViewportWidth(maximumWindowBounds.getWidth() - 15);

        Scene scene = new Scene(scrollPane, Color.WHITE); // 969faf
        scene.setFill(Color.WHITE);
        root = new Group();

        root.getChildren().add(gridPane);

        scrollPane.setContent(root);

        stage.setTitle("Wavefunction Collapse Algorithm");
        stage.getIcons().add(new Image("C:\\Users\\mnikitin\\IdeaProjects\\other\\demo\\src\\main\\resources\\com\\example\\demo\\icon.png"));
        stage.setScene(scene);
        stage.setMaximized(true);

        stage.show();


        final int delay = 200;

        StoppableThread thread11 = new StoppableThread(() -> {
            for (PowerNode node : matrix) {
                // TODO Для того, чтобы реализовать остановку выполнения программы, нужно не только сделать кастомный
                //  thread, но и сделать какой-нибудь класс, оборачивающий функционал текущего runnable, чтобы он имел
                //  ссылку на обромляющий его StoppableThread и мог обратиться к методу isStopped


                // TODO При добавлении нового элемента в матрицу, необходимо также переносить statusPane из удаляемого элемента
                delay(delay);

                List<VoltageLevel> voltageLevels = new ArrayList<>(Arrays.asList(VoltageLevel.values()));

                // Удаляем уровни напряжения, которые заблокированы
                node.getBasePane().getStatusPane().getStatuses().stream()
                    .filter(s -> BlockType.BLOCK.equals(s.getType().getBlockType()))
                    .filter(s -> PowerNodeType.SUBSTATION.equals(s.getType().getNodeType()))
                    .forEach(status -> voltageLevels.removeAll(status.getVoltageLevels()));

                // Для формирования Трансформатора нужно как минимум два уровня напряжения
                if (voltageLevels.size() <= 1) continue;

                int random1 = new Random().nextInt(voltageLevels.size() - 1) + 1;
                VoltageLevel level1 = voltageLevels.get(random1);
                voltageLevels.remove(level1);

                VoltageLevel level2 = voltageLevels.get(0);;

                if (voltageLevels.size() > 1) {
                    int random2 = new Random().nextInt(voltageLevels.size() - 1) + 1;
                    level2 = voltageLevels.get(random2);
                }


                fillWithSubstation(node, size, level1, level2, matrix, gridPane);
                PowerNode updatedNode = matrix.getNode(node.getX(), node.getY()).get();
                matrix.getArea(node.getX(), node.getY(), level1.getBoundingArea()).forEach(n -> n.addStatus(StatusType.BLOCK_SUBSTATION, true, level1));

                VoltageLevel finalLevel = level2;
                matrix.getArea(node.getX(), node.getY(), level2.getBoundingArea()).forEach(n -> n.addStatus(StatusType.BLOCK_SUBSTATION, true, finalLevel));
                matrix.getArea(node.getX(), node.getY(), 1)
                    .forEach(n -> n.addStatus(StatusType.BLOCK_SUBSTATION, true, VoltageLevel.values()));

                List<PowerNode> powerNodes = matrix.toOrderedNodeList();
                Collections.reverse(powerNodes);
                powerNodes.stream()
                    .filter(n -> n.getNodeType() != null)
                    .filter(n -> n.getX() <= updatedNode.getX() || n.getY() <= updatedNode.getY() && sqrt(pow(node.getX() - n.getX(), 2) + pow(node.getY() - n.getY(), 2)) <= 3 * level1.getBoundingArea()) //
                    .filter(n -> n.getConnectionPoints().containsKey(level1)).limit(2).forEach(n -> connectTwoNodesByConnectionPoints(root, updatedNode, n, level1, size));

            }
        });
        thread11.setDaemon(true);
        thread11.start();

        scene.setOnKeyPressed(keyEvent -> {
            if (KeyCode.CAPS.equals(keyEvent.getCode())) {
                thread11.changeStopped();
                System.out.println("Нажатие на CAPS");
                System.out.println("Thread 1 stopped = " + thread11.isStopped());
                // TODO вывести подсказку снизу экрана, которая бы затухала через несколько секунд
            }
        });

        Thread thread21 = new Thread(() -> {
            delay((rows * columns + 2) * delay);
            // TODO Сделать модели нагрузки и генератора и добавить их pane-ы после прохода первого треда, для демонстрации
        });
        thread21.setDaemon(true);
        thread21.start();


//        Thread thread = new Thread(() -> {
//
//            for (PowerNode node : matrix) {
//                try {
//                    Thread.sleep(delay);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//
//                int random1 = new Random().nextInt(3); // nextInt(value) возвращает число от 0 до value не включительно
//
//                if (random1 > 0) continue; // 33% на шанс выпадения substation
//
//                VoltageLevel[] values = VoltageLevel.values();
//                int random = new Random().nextInt(values.length - 1) + 1;
//
//                VoltageLevel highVoltage = values[random];
//                VoltageLevel lowVoltage;
//                if (random == 1) {
//                    lowVoltage = values[random - 1];
//                } else {
//                    lowVoltage = values[random - 2];
//                }
//
//                    Platform.runLater(() -> {
//                        SubStation subStation = new SubStation(size, highVoltage, lowVoltage);
//                        subStation.setX(node.getX());
//                        subStation.setY(node.getY());
//                        matrix.add(subStation);
//                        gridPane.add(subStation.getStackPane(), node.getY(), node.getX());
//                    });
//            }
//        });
//        thread.start();
//
//
//        Thread thread2 = new Thread(() -> {
//            try {
//                Thread.sleep(4000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//
//
//            VoltageLevel level = VoltageLevel.LEVEL_110;
//            List<PowerNode> powerNodes = matrix.toPowerNodeList().stream().filter(node -> node.getConnectionPoints().containsKey(level)).toList();
//
//            if (powerNodes.size() >= 2) {
////                connectTwoNodes(root, powerNode1, powerNode2);
//                connectTwoNodesByConnectionPoints(root, powerNodes.get(0), powerNodes.get(1), level, size);
//            }
//
//            Platform.runLater(() -> {
//
//                PowerNode powerNode = powerNodes.get(0);
//
//                powerNode.addStatus(StatusType.BLOCK_SUBSTATION, VoltageLevel.LEVEL_35);
//                powerNode.addStatus(StatusType.BLOCK_GENERATOR, VoltageLevel.LEVEL_35);
//                powerNode.addStatus(StatusType.BLOCK_LOAD, VoltageLevel.LEVEL_35);
//                powerNode.addStatus(StatusType.MUST_SUBSTATION, VoltageLevel.LEVEL_35);
//                powerNode.addStatus(StatusType.MUST_GENERATOR, VoltageLevel.LEVEL_35);
//                powerNode.addStatus(StatusType.MUST_LOAD, VoltageLevel.LEVEL_35);
//
//                powerNode.addStatus(StatusType.BLOCK_SUBSTATION, VoltageLevel.LEVEL_35);
//                powerNode.addStatus(StatusType.BLOCK_SUBSTATION, VoltageLevel.LEVEL_110);
//                powerNode.addStatus(StatusType.BLOCK_SUBSTATION, VoltageLevel.LEVEL_220);
//            });
//
//        });
//        thread2.start();

    }


    public static void fillPowerGridPaneAndMatrix(Matrix<PowerNode> matrix, GridPane grid, int rows, int columns, double value) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
//                StackPane stackPane = (i + j) % 2 == 0 ? elementsService.createGeneratorPane(size) : elementsService.createTransformerPane(size);
                PowerNode powerNode = new PowerNode(value);
                powerNode.setX(i);
                powerNode.setY(j);
                matrix.fill(powerNode);
                GridPane.setConstraints(powerNode.getStackPane(), j, i);
                grid.getChildren().add(powerNode.getStackPane());
            }
        }
    }


    public static void connectTwoNodesByConnectionPoints(Group root, PowerNode node1, PowerNode node2, VoltageLevel voltageLevel, double size) {
        ConnectionPoint connectionPoint1 = node1.getConnectionPoints().get(voltageLevel);
        ConnectionPoint connectionPoint2 = node2.getConnectionPoints().get(voltageLevel);

        if (connectionPoint1 != null && connectionPoint2 != null) {
            Bounds boundsP1 = node1.getStackPane().getBoundsInParent();
            Bounds boundsP2 = node2.getStackPane().getBoundsInParent();

            Platform.runLater(() -> {
                Line line1 = new Line();
                line1.setStartX(boundsP1.getCenterX() + connectionPoint1.x());
                line1.setStartY(boundsP1.getCenterY() + connectionPoint1.y());
                line1.setEndX(boundsP2.getCenterX() + connectionPoint2.x());
                line1.setEndY(boundsP2.getCenterY() + connectionPoint2.y());
                line1.setStroke(voltageLevel.getColor());
                line1.setStrokeWidth(size * 8 / 300);
                line1.setOpacity(0.5d);
//                line1.toBack(); // TODO Разобраться, как вывести некоторые элементы на передний план (кольца трансформатора и статусы), а линию убрать на задний план


                root.getChildren().add(line1);
            });
        }


    }

    public void delay(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void fillWithSubstation(PowerNode node, double size,
                                   VoltageLevel highVoltage, VoltageLevel lowVoltage,
                                   Matrix<PowerNode> matrix, GridPane gridPane) {
        Optional<PowerNode> oldValue = matrix.getNode(node.getX(), node.getY());

        SubStation subStation = new SubStation(size, highVoltage, lowVoltage);
        subStation.setX(node.getX());
        subStation.setY(node.getY());
        matrix.add(subStation);

        Platform.runLater(() -> {
            gridPane.add(subStation.getStackPane(), node.getY(), node.getX());
        });
    }


}