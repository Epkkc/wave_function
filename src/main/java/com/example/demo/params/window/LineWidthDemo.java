package com.example.demo.params.window;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LineWidthDemo extends Application {

    private final double totalEndX = 500;
    private final double totalStartX = 200;
    boolean spaceActive = false;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Line line = new Line();
        line.setStroke(Color.BLUE);
        line.setStrokeWidth(10);

        line.setStartY(10);
        line.setStartX(10);

        line.setEndX(totalEndX);
        line.setEndY(10);

        Group group = new Group(line);

        Scene scene = new Scene(group);

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();

        System.out.println(primaryStage.getHeight()); // Для того, чтобы установить максимальный размер нужно приконнектиться к проперти height и width у stage
        System.out.println(primaryStage.getWidth());
        scene.setOnKeyPressed(keyEvent -> {
                if (KeyCode.SPACE.equals(keyEvent.getCode())) {
                    if (spaceActive) {
                        Timeline timeline = new Timeline();
                        timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO, new KeyValue(line.endXProperty(), totalStartX)));
                        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500), new KeyValue(line.endXProperty(), totalEndX)));
                        timeline.play();
                    } else {
                        Timeline timeline = new Timeline();
                        timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO, new KeyValue(line.endXProperty(), totalEndX)));
                        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500), new KeyValue(line.endXProperty(), totalStartX)));
                        timeline.play();
                    }
                    changeSpaceFlag();
                }
            }
        );

//        scene.setOnKeyPressed(keyEvent -> {
//                if (KeyCode.CAPS.equals(keyEvent.getCode())) {
//                    if (spaceActive) {
//                        line.setEndX(500);
//
//                    } else {
//                        line.setEndX(200);
//                    }
//                    changeSpaceFlag();
//                }
//            }
//        );

    }

    private void changeSpaceFlag() {
        System.out.println(spaceActive);
        spaceActive = !spaceActive;
    }
}
