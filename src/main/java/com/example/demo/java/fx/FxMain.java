package com.example.demo.java.fx;

import com.example.demo.base.model.configuration.GeneralResult;
import com.example.demo.java.fx.service.FxAlgorithmService;
import com.example.demo.thread.StoppableThread;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.concurrent.FutureTask;

public class FxMain extends Application {

    static int rows = 16;
    static int columns = 30;
    static int numberOfNodes = 50;
    static int numberOfEdges = 100;

    public static void main(String[] args) {
        launch();

    }

    @Override
    public void start(Stage primaryStage) {

        FxAlgorithmService algorithmService = new FxAlgorithmService(rows, columns, 2000, numberOfNodes, numberOfEdges);

        Group sceneRoot = new Group();

        StackPane stopMessage = getStopMessageBlock("Для продолжения работы нажмите CAPS LOCK");
        stopMessage.setAlignment(Pos.BOTTOM_CENTER);
        stopMessage.setOpacity(0);
        stopMessage.mouseTransparentProperty().set(true);
        sceneRoot.getChildren().add(stopMessage);

        Scene scene = new Scene(sceneRoot, Color.WHITE); // 969faf
        scene.setFill(Color.WHITE);

        FutureTask<GeneralResult> future = new FutureTask<>(() -> algorithmService.startAlgo(sceneRoot));

        StoppableThread thread = new StoppableThread(future);
        thread.setName("Didli");
        thread.setDaemon(true);

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

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);

//        primaryStage.setTitle("Wavefunction Collapse Algorithm");
//        primaryStage.getIcons().add(new Image("C:\\Users\\mnikitin\\IdeaProjects\\other\\demo\\src\\main\\resources\\com\\example\\demo\\icon.png"));
//        primaryStage.show();

        thread.start();

        // todo обработать получение результата
    }

    private StackPane getStopMessageBlock(String message) {
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
